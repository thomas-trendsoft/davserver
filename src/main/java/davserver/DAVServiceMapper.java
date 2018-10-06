package davserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import davserver.protocol.DAVACL;
import davserver.protocol.DAVBind;
import davserver.protocol.DAVCopy;
import davserver.protocol.DAVDelete;
import davserver.protocol.DAVGet;
import davserver.protocol.DAVLock;
import davserver.protocol.DAVMkCol;
import davserver.protocol.DAVOptions;
import davserver.protocol.DAVPropFind;
import davserver.protocol.DAVPropPatch;
import davserver.protocol.DAVPut;
import davserver.protocol.DAVReport;
import davserver.protocol.DAVRequest;
import davserver.protocol.auth.IAuthenticationProvider;
import davserver.protocol.auth.Session;
import davserver.protocol.auth.SessionStore;
import davserver.repository.IRepository;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

// TODO Make Error Handling with multiple messages easier or enable it at least


/**
 * Basic Http service mapper to configured repositories 
 * 
 * @author tkrieger
 *
 */
public class DAVServiceMapper implements HttpAsyncRequestHandler<HttpRequest> {
	
	private static final Pattern PSession = Pattern.compile("DAVSESSID=(?<sid>[^;\\s]*)");
	
	/**
	 * URL Prefix
	 */
	private String prefix;
	
	/**
	 * Given Repositories
	 */
	private HashMap<String, IRepository> repositories;
	
	/**
	 * Debugging flag
	 */
	private static boolean debug = true;
	
	/**
	 * Mapping of the method implementations
	 */
	private HashMap<String,DAVRequest> methods;
	
	/**
	 * Session store 
	 */
	private SessionStore sessions;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVServiceMapper(String prefix) {

		// sample config (later through api)
		this.prefix = prefix;
		this.repositories  = new HashMap<String, IRepository>();
		
		// create protocol method map
		DAVLock lock = new DAVLock();
		DAVGet  get  = new DAVGet();
		DAVCopy copy = new DAVCopy();
		
		methods   = new HashMap<String,DAVRequest>();
		methods.put("PROPFIND",new DAVPropFind());
		methods.put("LOCK",lock);
		methods.put("UNLOCK",lock);
		methods.put("PUT",new DAVPut());
		methods.put("GET",get);
		methods.put("HEAD",get);
		methods.put("MKCOL",new DAVMkCol());
		methods.put("DELETE",new DAVDelete());
		methods.put("OPTIONS",new DAVOptions());
		methods.put("COPY",copy);
		methods.put("MOVE",copy);
		methods.put("PROPPATCH", new DAVPropPatch());
		methods.put("BIND", new DAVBind());
		methods.put("REPORT", new DAVReport());
		methods.put("ACL",new DAVACL());
		
		this.sessions = new SessionStore();
		
	}
	
	private void debug(HttpResponse resp) {
		System.out.println("-------------------------------------- \nRESPONSE:");
		System.out.println(resp.getStatusLine().toString());
		Header[] headers = resp.getAllHeaders();
		for (Header h : headers) {
			System.out.println(h.getName() + ": " + h.getValue());
		}
	}
	
	private void debug(HttpRequest req) {
		System.out.println("-------------------------------------- \nREQUEST:\n");
		System.out.println(req.getRequestLine().toString());
		
		Header[] headers = req.getAllHeaders();
		for (Header h : headers) {
			System.out.println(h.getName() + ": " + h.getValue());			
		}		
	}
	
	/**
	 * Main Request Handling Entry
	 * 
	 * @param req HttpRequest
	 * @param async HttpAsyncExchange Context
	 * @param ctx HttpContext
	 */
	public void handle(HttpRequest req, HttpAsyncExchange async, HttpContext ctx) throws HttpException, IOException {
		boolean authFailed = false;
		
		if (debug) this.debug(req);
		
		final HttpResponse response = async.getResponse();
				
		// check target url
		DAVUrl durl = new DAVUrl(req.getRequestLine().getUri(),prefix);
		System.out.println("create durl:  "+ durl.getResref());
		IRepository repos = null;
		if (durl.getRepository() != null) {
			repos = repositories.get(durl.getRepository());
		}

		System.out.println("CHECK AUTH: ");
		// check auth if needed
		if (repos != null && repos.needsAuth()) {
			if (!getAuthentication(req,response,repos.getAuthProvider())) {
				authFailed = true;
			}
		}
		
		String method = req.getRequestLine().getMethod();

		try {
			if (authFailed) {
				repos.getAuthProvider().rejectedResponse(req,response);
				throw new DAVException(401, "not allowed");
			} else if (req.getRequestLine().getUri().indexOf("#") > 0) {
				// disallow fragments on request url
				throw new DAVException(400,"fragment in request url");
			} else if (repos == null) {
				// 404 if no repository is found
				response.setStatusCode(404);	
			} else if (methods.containsKey(method)) {
				DAVRequest handler = methods.get(method);
				handler.handle(req, response, repos, durl);
			} else {
				async.getResponse().setStatusCode(404);			
			}			
		} catch (DAVException e) {
			DAVUtil.handleError(e, response);
		} catch (NotFoundException e) {
			DAVUtil.handleError(new DAVException(404,e.getMessage()),response);
		} catch (NotAllowedException e) {
			DAVUtil.handleError(new DAVException(403,e.getMessage()),response);
		}
		
		if (debug) debug(response);
		
		// close connection if unexpected body is not consumed
		if (req instanceof HttpEntityEnclosingRequest) {
			EntityUtils.consumeQuietly(((HttpEntityEnclosingRequest)req).getEntity());			
		}

		async.submitResponse(new BasicAsyncResponseProducer(response));
	}
	
	/**
	 * Check session for auth neede repos 
	 * 
	 * @param req
	 * @param authProvider
	 * @return
	 */
	private boolean getAuthentication(HttpRequest req, HttpResponse resp,IAuthenticationProvider authProvider) {
		Session session = null;
		Header  cookie = req.getFirstHeader("Cookie");
		String  sid = null;
		
		if (cookie == null) {
			session = sessions.create(req);
			resp.addHeader("Set-Cookie","DAVSESSID=" + session.getId());
			return false;
		}
		
		// Parse session ID
		Matcher m = PSession.matcher(cookie.getValue());
		if (m.find()) {
			sid = m.group("sid");
		}
		
		// got session id then check for active session
		if (sid != null) {
			session = sessions.get(sid);
		}
		
		// still no session?
		if (session == null) {
			session = sessions.create(req);
			resp.addHeader("Set-Cookie","DAVSESSID=" + session.getId());
			return false;			
		}
		
		return authProvider.authRequest(req, session);

	}

	@Override 
	public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest req, HttpContext ctx)
			throws HttpException, IOException {
		return new BasicAsyncRequestConsumer();
	}
	
	/**
	 * add a repository to the service mapper 
	 * 
	 * @param name
	 * @param repos
	 */
	public void addRepository(String name,IRepository repos) {
		this.repositories.put(name, repos);
	}
	
	/**
	 * Use individual session store 
	 * 
	 * @param store
	 */
	public void setSessionStore(SessionStore store) {
		this.sessions = store;
	}

}
