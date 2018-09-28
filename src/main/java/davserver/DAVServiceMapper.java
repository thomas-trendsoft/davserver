package davserver;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

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
import davserver.repository.IRepository;
import davserver.repository.cal.SimpleCalDAVRepository;
import davserver.repository.card.SimpleCardDAVRepository;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.simple.SimpleRepository;

// TODO Make Error Handling with multiple messages easier or enable it at least
public class DAVServiceMapper implements HttpAsyncRequestHandler<HttpRequest> {
	
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
	 * Defaultkonstruktor 
	 */
	public DAVServiceMapper() {
		this.prefix = "";
		this.repositories  = new HashMap<String, IRepository>();
		this.repositories.put("simple", new SimpleRepository());
		this.repositories.put("contacts", new SimpleCardDAVRepository());
		this.repositories.put("calendars", new SimpleCalDAVRepository());
		
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
		
		if (debug) this.debug(req);
		
		final HttpResponse response = async.getResponse();
		
		DAVUrl durl = new DAVUrl(req.getRequestLine().getUri(),prefix);
		IRepository repos = null;
		if (durl.getRepository() != null) {
			repos = repositories.get(durl.getRepository());
		}

		// dev outs
		System.out.println("durl: " + durl.getRepository() + ":" + durl.getResref());
		System.out.println("repos: " + repos);

		String method = req.getRequestLine().getMethod();

		try {
			if (req.getRequestLine().getUri().indexOf("#") > 0) {
				// disallow fragments on request url
				DAVUtil.handleError(new DAVException(400,"fragment in request url"), response);
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

		async.submitResponse(new BasicAsyncResponseProducer(response));
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
		
	}

}
