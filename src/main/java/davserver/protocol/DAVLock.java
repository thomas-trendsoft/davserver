package davserver.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.protocol.xml.ListElement;
import davserver.repository.ILockManager;
import davserver.repository.IRepository;
import davserver.repository.LockEntry;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.ResourceExistsException;
import davserver.utils.XMLParser;

/**
 * HTTP LOCK Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVLock {

	private boolean debug;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVLock() {
		this.debug = true;
	}
	
	/**
	 * Simple lock token creation
	 * 
	 * @param url
	 * @return
	 */
	private String createLockToken(DAVUrl url) {
		return "opaquelocktoken:" + UUID.randomUUID().toString();
	}
	
	/**
	 * Get or create the resource for the lock request 
	 * 
	 * @param repos
	 * @param ref
	 * @param resp
	 * @return
	 */
	private Resource getLockResource(IRepository repos,String ref,HttpResponse resp) {
		
		// check if target exists
		Resource target = null;
		try {
			target = repos.locate(ref);			
		} catch (NotAllowedException e) {
			DAVUtil.handleError(new DAVException(403,"not allowed"), resp);
			return null;
		} catch (NotFoundException e) {
		}
		
		// found some results
		if (target != null) {
			return target;
		}
		
		// create empty resource if not exists
		try {
			target = repos.createResource(ref, new ByteArrayInputStream(new byte[0]));
			resp.setStatusCode(201);
		} catch (NotAllowedException e2) {
			DAVUtil.handleError(new DAVException(403,"not allowed"), resp);
			return null;
		} catch (ConflictException e2) {
			DAVUtil.handleError(new DAVException(409,"conflict on create"), resp);
			return null;
		} catch (ResourceExistsException e2) {
			DAVUtil.handleError(new DAVException(409,"conflict on create"), resp);
			return null;
		} catch (NotFoundException e2) {
			DAVUtil.handleError(new DAVException(409,"conflict on create"), resp);
			return null;
		} catch (IOException e2) {
			DAVUtil.handleError(new DAVException(500,e2.getMessage()), resp);
			return null;
		}	
		
		return target;
	}

	/**
	 * Handle HTTP LOCK Method
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param r Resource
	 * @param url DAV URL
	 */
	public void handleLock(HttpEntityEnclosingRequest req,HttpResponse resp,IRepository repos,DAVUrl url) {
		int          depth;
		ILockManager lm;
		
		// check if locks are supported
		if (!repos.supportLocks()) {
			System.out.println("no lock support");
			DAVUtil.handleError(new DAVException(415,"Locks are not supported inside this repository"),resp);
			return;
		}
		
		lm = repos.getLockManager();
		
		// check depth header param
		Header d = req.getFirstHeader("Depth");
		if (d == null || d.getValue().compareTo("infinity")==0) {
			depth = Integer.MAX_VALUE;
		} else {
			try {
				depth = Integer.parseInt(d.getValue());
			} catch (NumberFormatException nfe) {
				DAVUtil.handleError(new DAVException(400,"bad request"), resp);
				return;
			}
		}
				
		// check lock info body
		LockEntry le    = null;
		String    token = createLockToken(url); 
		if (req.getEntity().getContentLength() > 0) {
			try {
				Document body = XMLParser.singleton().parseStream(req.getEntity().getContent());
				System.out.println("got a lock body: " + body);
				if (debug) { DAVUtil.debug(body); }
				// read requested lock properties
				le = LockEntry.parse(url.getResref(),body,depth,token);
			} catch (UnsupportedOperationException e) {
				resp.setStatusCode(400);
				e.printStackTrace();
			} catch (SAXException e) {
				resp.setStatusCode(400);
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				resp.setStatusCode(500);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				resp.setStatusCode(500);
			} 
		} else {
			DAVUtil.handleError(new DAVException(400,"No lock request body"),resp);
			return;
		}
		
		// check parsed response
		if (le == null) {
			DAVUtil.handleError(new DAVException(400,"bad request"), resp);
			return;
		}

		// get or create resource
		Resource target = getLockResource(repos, url.getResref(), resp);
		if (target == null)
			return;

		// create lock entry
		LockEntry lock  = null;
		try {
			lock = lm.registerLock(le);
		} catch (LockedException e1) {
			DAVUtil.handleError(new DAVException(409,"is already locked"), resp);
			return;
		}

		// create response
		try {
			ListElement  mr    = new ListElement("prop", DAVServer.Namespace);
			
			mr.addChild(lock);
			
			String xmlDoc = XMLParser.singleton().serializeDoc(mr.createDocument());
			
			if (debug) { System.out.println(xmlDoc); }
			
			resp.addHeader("Lock-Token","<" + lock.getToken() + ">");
			
			resp.setEntity(new StringEntity(xmlDoc,"utf-8"));
			System.out.println("lock done");
		} catch (Exception e) {
			e.printStackTrace();
			DAVUtil.handleError(new DAVException(500,e.getMessage()), resp);
			return;
		}
	}
}
