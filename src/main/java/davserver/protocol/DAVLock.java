package davserver.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.protocol.header.IfHeader;
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
	
	private void responseLock(HttpResponse resp,LockEntry lock) {
		// create response
		try {
			ListElement  mr    = new ListElement("prop", DAVServer.Namespace);
			
			mr.addChild(lock);
			
			String xmlDoc = XMLParser.singleton().serializeDoc(mr.createDocument());
			
			if (debug) { System.out.println(xmlDoc); }
			
			resp.addHeader("Lock-Token","<" + lock.getToken() + ">");
			
			resp.setEntity(new StringEntity(xmlDoc,"utf-8"));
			resp.setHeader("Content-Type","application/xml;charset=utf-8");

			System.out.println("lock done");
		} catch (Exception e) {
			e.printStackTrace();
			DAVUtil.handleError(new DAVException(500,e.getMessage()), resp);
			return;
		}
		
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
		} 
		
		// check for refresh header
		Header lheader = req.getFirstHeader("If");
		if (le == null && lheader != null) {
			System.out.println("check refresh");
			try {
				LockEntry lock = lm.checkLocked(url.getResref());
				IfHeader  rif  = IfHeader.parseIfHeader(lheader.getValue());
				
				if (lock == null) {
					System.out.println("no lock");
					DAVUtil.handleError(new DAVException(400,"no lock"), resp);
					return;
				}
				if (!rif.evaluate(lock.getToken(), null)) {
					System.out.println("wrong lock token");
					DAVUtil.handleError(new DAVException(423,"wrong lock token"), resp);
					return;
				}
				lock.setTimeout(LockEntry.updatedTimeout());
				repos.getLockManager().updateLock(lock);
				// response the lock
				responseLock(resp, lock);
				System.out.println("ret lock refresh");
				return;
			} catch (ParseException e) {
				DAVUtil.handleError(new DAVException(400,"bad request if"), resp);
				return;
			}
			
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
			lock = lm.checkLocked(url.getResref());
			System.out.println(lock.isShared() + ":" + le.isShared());
			// TODO Check when to update and when to conflict (litmus no lock-token only owner as "auth"?)
			// includes exclusive share?
			if (!lock.isShared() || !le.isShared()) {
				// check if includes owner
				for (String o : le.getOwner()) {
					if (!lock.getOwner().contains(o)) {
						DAVUtil.handleError(new DAVException(423,"is already locked"), resp);
						return;					
					}
				}
			} else if (lock.isShared() && le.isShared()) {
				le.getOwner().addAll(lock.getOwner());
				lm.updateLock(le);
			}
		}
		
		// response the lock
		responseLock(resp, lock);

	}
	
	public void handleUnlock(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url) throws DAVException {
		System.out.println("handle unlock");
		
		if (!repos.supportLocks()) {
			throw new DAVException(425,"unsupported");
		}
		
		LockEntry le = repos.getLockManager().checkLocked(url.getResref());
		if (le == null) {
			throw new DAVException(409,"no lock given");
		}
		
		// check lock header against lock entry
		try {
			Resource   r = repos.locate(url.getResref());
			Header   hif = req.getFirstHeader("If");
			// check if header
			if (hif == null) {
				// check lock token header
				Header hlt = req.getFirstHeader("Lock-Token");
				if (hlt != null && hlt.getValue().length() > 3) {
					String check = hlt.getValue().substring(1, hlt.getValue().length()-1);
					System.out.println("lheader check: " + check + "  ==  " + le.getToken());
					if (check.compareTo(le.getToken()) != 0) {
						throw new DAVException(423,"wrong lock token submitted");											
					}
				} else {
					throw new DAVException(423,"no lock token submitted");					
				}
			} else {
				IfHeader lh = IfHeader.parseIfHeader(hif.getValue());

				if (!lh.evaluate(le.getToken(), (r != null ? r.getETag() : null))) {
					throw new DAVException(409,"precondition failed");
				}				
			}
			repos.getLockManager().removeLock(le);
		} catch (ParseException pe) {
			throw new DAVException(400,"bad if header");
		} catch (NotFoundException e) {
			throw new DAVException(404,"not found");
		} catch (NotAllowedException e) {
			throw new DAVException(405,"not allowed");
		}
		
		resp.setStatusCode(204);
	}
}
