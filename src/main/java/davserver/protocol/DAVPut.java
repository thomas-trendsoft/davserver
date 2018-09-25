package davserver.protocol;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
import davserver.repository.LockEntry;
import davserver.repository.error.ConflictException;
import davserver.repository.error.RepositoryException;
import davserver.repository.error.ResourceExistsException;

/**
 * HTTP PUT Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVPut {

	
	/**
	 * Handle HTTP PUT Method
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param r Resource
	 * @param url DAV URL
	 */
	public void handlePut(HttpEntityEnclosingRequest req,HttpResponse resp,IRepository repos,DAVUrl url) {
		System.out.println("handle put request");
		
		if (url.getResref() == null) {
			DAVUtil.handleError(new DAVException(400,"bad request"), resp);
			return;
		}
		
		try {
			// check preconditions
			try {
				DAVRequest.checkLock(req, repos, url);
			} catch (DAVException e) {
				e.printStackTrace();
				DAVUtil.handleError(e, resp);
				return;
			}
			
			// create resource
			repos.createResource(url.getResref(),req.getEntity().getContent());
			resp.setStatusCode(201);
			System.out.println(repos.toString());
		} catch (ConflictException ce) {
			DAVUtil.handleError(new DAVException(409, ce.getMessage()),resp);
			return;
		} catch (ResourceExistsException ee) {
			DAVUtil.handleError(new DAVException(405, ee.getMessage()),resp);
			return;			
		} catch (RepositoryException re) {
			re.printStackTrace();
			DAVUtil.handleError(new DAVException(500,re.getMessage()),resp);
			return;
		} catch (IOException e) {
			e.printStackTrace();
			DAVUtil.handleError(new DAVException(500,e.getMessage()),resp);
			return;
		}
		
	}
	
}
