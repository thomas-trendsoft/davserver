package davserver.protocol;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.repository.IRepository;
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
	 * @throws DAVException 
	 */
	public void handlePut(HttpEntityEnclosingRequest req,HttpResponse resp,IRepository repos,DAVUrl url) throws DAVException {
		System.out.println("handle put request");
		
		if (url.getResref() == null) {
			throw new DAVException(400,"bad request");
		}
		
		try {
			// check preconditions
			DAVRequest.checkLock(req, repos, url);
			
			// create resource
			repos.createResource(url.getResref(),req.getEntity().getContent());
			resp.setStatusCode(201);
			System.out.println(repos.toString());
		} catch (ConflictException ce) {
			throw new DAVException(409, ce.getMessage());
		} catch (ResourceExistsException ee) {
			throw new DAVException(405, ee.getMessage());
		} catch (RepositoryException re) {
			re.printStackTrace();
			throw new DAVException(500,re.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		}
		
	}
	
}
