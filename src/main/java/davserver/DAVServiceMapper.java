package davserver;

import java.io.IOException;
import java.util.HashMap;

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
import davserver.repository.IRepository;
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
	 * PROPFIND Implementation
	 */
	private DAVPropFind propfind;
	
	/**
	 * PROFPATCH Implementation
	 */
	private DAVPropPatch proppatch;
	
	/**
	 * LOCK Implementation
	 */
	private DAVLock lock;
	
	/**
	 * GET Implementation
	 */
	private DAVGet get;
	
	/**
	 * PUT Implementation
	 */
	private DAVPut put;
	
	/**
	 * MKCOL Implementation
	 */
	private DAVMkCol mkcol;
	
	/**
	 * DELETE Implementation
	 */
	private DAVDelete delete;
	
	/**
	 * OPTIONS Implementation
	 */
	private DAVOptions options;
	
	/**
	 * COPY Implementation
	 */
	private DAVCopy copy;
	
	/**
	 * BIND Implementation
	 */
	private DAVBind bind;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVServiceMapper() {
		this.prefix = "";
		this.repositories  = new HashMap<String, IRepository>();
		this.repositories.put("simple", new SimpleRepository());
		
		propfind  = new DAVPropFind();
		lock      = new DAVLock();
		put       = new DAVPut();
		get       = new DAVGet();
		mkcol     = new DAVMkCol();
		delete    = new DAVDelete();
		options   = new DAVOptions();
		copy      = new DAVCopy();
		proppatch = new DAVPropPatch();
		bind      = new DAVBind();
		
	}
	
	private void debug(HttpResponse resp) {
		System.out.println("RESPONSE:\n");
		System.out.println(resp.getStatusLine().toString());
		Header[] headers = resp.getAllHeaders();
		for (Header h : headers) {
			System.out.println(h.getName() + ": " + h.getValue());
		}
	}
	
	private void debug(HttpRequest req) {
		System.out.println("REQUEST:\n");
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
			} else if (method.startsWith("PROP")) {
				if (method.compareTo("PROPFIND")==0) {
					propfind.handlePropFind((HttpEntityEnclosingRequest)req, response, repos,durl);
				} else if (method.compareTo("PROPPATCH")==0) {
					proppatch.handlePropPatch((HttpEntityEnclosingRequest)req,response,repos,durl);
				}
			} else if (method.compareTo("GET")==0) {
				get.handleGet(req,response, repos,durl,false);
			} else if (method.compareTo("HEAD")==0) {
				get.handleGet(req, response, repos, durl, true);
			} else if (method.compareTo("OPTIONS")==0) {
				System.out.println("options prepare: " + durl.getResref());
				options.handleOptions(req, response, repos,durl);
			} else if (method.compareTo("COPY")==0) {
				copy.handleCopy(req,response,repos,durl,false);
			} else if (method.compareTo("MOVE")==0) {
				copy.handleCopy(req, response, repos, durl, true);
			} else if (method.compareTo("DELETE")==0) {
				delete.handleDelete(req, response, repos,durl);
			} else if (method.compareTo("MKCOL")==0) {
				mkcol.handleMkCol((HttpEntityEnclosingRequest)req,response, repos,durl);
			} else if (method.compareTo("PUT")==0) {
				put.handlePut((HttpEntityEnclosingRequest)req,response,repos,durl);
			} else if (method.compareTo("BIND")==0) {
				bind.handleBind(req, response, repos, durl);
			} else if (method.compareTo("LOCK")==0) {
				lock.handleLock((HttpEntityEnclosingRequest)req,response,repos,durl);
			} else if (method.compareTo("UNLOCK")==0) {
				lock.handleUnlock(req, response, repos, durl);
			} else {
				async.getResponse().setStatusCode(404);			
			}			
		} catch (DAVException e) {
			DAVUtil.handleError(e, response);
		} catch (NotFoundException e) {
			DAVUtil.handleError(new DAVException(404,"not found"),response);
		} catch (NotAllowedException e) {
			DAVUtil.handleError(new DAVException(403,"not allowed"),response);
		}
		
		if (debug) debug(response);

		System.out.println("submit resp");
		async.submitResponse(new BasicAsyncResponseProducer(response));
	}

	public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest req, HttpContext ctx)
			throws HttpException, IOException {
		return new BasicAsyncRequestConsumer();
	}

}
