package davserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import davserver.protocol.DAVDelete;
import davserver.protocol.DAVGet;
import davserver.protocol.DAVLock;
import davserver.protocol.DAVMkCol;
import davserver.protocol.DAVOptions;
import davserver.protocol.DAVPropFind;
import davserver.protocol.DAVPut;
import davserver.repository.IRepository;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.LockedException;
import davserver.repository.error.RepositoryException;
import davserver.repository.error.ResourceExistsException;
import davserver.repository.simple.SimpleRepository;
import davserver.utils.XMLParser;

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
	 * Defaultkonstruktor 
	 */
	public DAVServiceMapper() {
		this.prefix = "";
		this.repositories  = new HashMap<String, IRepository>();
		this.repositories.put("simple", new SimpleRepository());
		
		propfind = new DAVPropFind();
		lock     = new DAVLock();
		put      = new DAVPut();
		get      = new DAVGet();
		mkcol    = new DAVMkCol();
		delete   = new DAVDelete();
		options  = new DAVOptions();
		
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

		// 404 if no repository is found
		if (repos == null) {
			response.setStatusCode(404);			
		} else if (method.compareTo("PROPFIND")==0) {
			propfind.handlePropFind((HttpEntityEnclosingRequest)req, response, repos,durl);
		} else if (method.compareTo("GET")==0) {
			get.handleGet(req,response, repos,durl);
		} else if (method.compareTo("OPTIONS")==0) {
			System.out.println("options prepare: " + durl.getResref());
			options.handleOptions(req, response, repos,durl);
		} else if (method.compareTo("DELETE")==0) {
			delete.handleDelete(req, response, repos,durl);
		} else if (method.compareTo("MKCOL")==0) {
			mkcol.handleMkCol(req,response, repos,durl);
		} else if (method.compareTo("PUT")==0) {
			put.handlePut((HttpEntityEnclosingRequest)req,response,repos,durl);
		} else if (method.compareTo("LOCK")==0) {
			lock.handleLock((HttpEntityEnclosingRequest)req,response,repos,durl);
		} else {
			async.getResponse().setStatusCode(404);			
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
