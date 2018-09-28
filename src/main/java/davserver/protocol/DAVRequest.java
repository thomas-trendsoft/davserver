package davserver.protocol;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.protocol.header.IfHeader;
import davserver.repository.IRepository;
import davserver.repository.LockEntry;
import davserver.repository.Resource;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

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
	public abstract void handle(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url) throws DAVException,NotFoundException,NotAllowedException;
	
	/**
	 * Help method to test lock (precondition)
	 * 
	 * @param req
	 * @param repos
	 * @param url
	 * @throws DAVException
	 */
	public static void checkLock(HttpRequest req,IRepository repos,DAVUrl url) throws DAVException {
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
			le = repos.getLockManager().checkLocked(url.getResref());
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
				if (lh.getResource() == null && lh.getConditions() == null) {
					throw new DAVException(423,"bad request");
				}
				// check or get resource tagged
				if (lh.getResource() != null) {
					DAVUrl curl = new DAVUrl(lh.getResource().getPath(), url.getPrefix());
					if (url.getResref().compareTo(curl.getResref())!=0) {
						throw new DAVException(423,"bad if uri");
					}						
				}
				// evaluate header conditions
				HashSet<String> tokens = lh.evaluate(le, (r != null ? r.getETag() : null));
				
				// eval result
				if (tokens == null) {
					throw new DAVException(423,"precondition failed");
				} else {
					for (String t : tokens) {
						System.out.println("success token: " + t);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
				throw new DAVException(400,"bad request");
			}			
		}
	}

}
