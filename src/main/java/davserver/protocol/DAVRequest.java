package davserver.protocol;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.transform.TransformerException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.NStringEntity;
import org.pmw.tinylog.Logger;
import org.w3c.dom.Document;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.protocol.auth.Session;
import davserver.protocol.header.IfCondition;
import davserver.protocol.header.IfHeader;
import davserver.repository.IRepository;
import davserver.repository.LockEntry;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.XMLParser;

/**
 * Helper class for common request functions 
 * 
 * @author tkrieger
 *
 */
public abstract class DAVRequest {
	
	/**
	 * Handle request implementation
	 * 
	 * @param req
	 * @param resp
	 * @param repos
	 * @param url
	 * @throws DAVException
	 */
	public abstract void handle(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url,Session session) throws DAVException,NotFoundException,NotAllowedException,ConflictException;
	
	/**
	 * Help method to test lock (precondition)
	 * 
	 * @param req
	 * @param repos
	 * @param url
	 * @throws DAVException
	 */
	public static void checkLock(HttpRequest req,IRepository repos,DAVUrl url,boolean child) throws DAVException {
		Resource r = null;
		
		// get resource 
		try {
			r = repos.locate(url.getResref());			
		} catch (NotAllowedException e) {
			
		} catch (NotFoundException e) {
			
		}
		
		// check locks
		HashMap<String,LockEntry> le = null;
		if (repos.supportLocks()) {
			le = repos.getLockManager().checkLocked(url.getResref(),child);
		}  
		
		System.out.println("lock entries: " + le);
		if (le != null) {
			System.out.println("size: " + le.size());
		}
		
		// check precondition
		Header hif = req.getFirstHeader("If");
		if (hif == null) {
			if (le != null && le.size() > 0)
				throw new DAVException(423,"no lock token submitted");
		} else {
			try {
				IfHeader lh = IfHeader.parseIfHeader(hif.getValue());
				
				// check header info
				if (lh.getConditions() == null) {
					throw new DAVException(412,"bad request");
				}

				// evaluate header conditions
				HashSet<IfCondition> tokens = lh.evaluate(le, r, repos,url);
				
				// eval result
				if (tokens == null) {
					throw new DAVException(412,"precondition failed");
				} else {
					for (IfCondition t : tokens) {
						System.out.println("success token: " + t.entity + ":" + t.state);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
				throw new DAVException(400,"bad request");
			}			
		}
	}
	
	protected void respondXML(int status,Document doc,HttpResponse resp) throws DAVException {

		System.out.println("respond xml");
		try {
			String retval = XMLParser.singleton().serializeDoc(doc);

			Logger.debug(status + ":" + retval);
			
			resp.setStatusCode(status);
			resp.setEntity(new NStringEntity(retval, "utf-8"));
			resp.setHeader("Content-Type","application/xml;charset=utf-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		}		
	}

}
