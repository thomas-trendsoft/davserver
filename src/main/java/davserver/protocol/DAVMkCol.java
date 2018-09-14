package davserver.protocol;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.RepositoryException;
import davserver.repository.error.ResourceExistsException;

/**
 * HTTP MKCOL Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVMkCol {
	
	/**
	 * Make Collection Implementation
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param url DAV Url
	 */
	public void handleMkCol(HttpEntityEnclosingRequest req,HttpResponse resp,IRepository repos,DAVUrl url) {
		System.out.println("handle mkcol");
		Resource r = null;
		
		// check if a body is given
		if (req.getEntity().getContentLength() > 0) {
			DAVUtil.handleError(new DAVException(415,"No supported body mkcol"), resp);
			return;
		}
		
		// Check if a resource exits add the location
		if (url.getResref() == null) {
			DAVUtil.handleError(new DAVException(400,"bad request"), resp);
			return;
		}
		
		// locate resource to check is not existing
		try {
			r = repos.locate(url.getResref());
		} catch (NotFoundException e) {
			r = null;
		} catch (NotAllowedException e) {
			DAVUtil.handleError(new DAVException(403,"not allowed"), resp);
			return;
		}
		
		if (r != null) {
			resp.setStatusCode(405);
			return;
		}
		
		// create new collection
		try {
			repos.createCollection(url.getResref());
			resp.setStatusCode(201);
			System.out.println(repos.toString());
		} catch (ConflictException ce) {
			DAVUtil.handleError(new DAVException(409, ce.getMessage()),resp);
			return;
		} catch (ResourceExistsException ee) {
			DAVUtil.handleError(new DAVException(405, ee.getMessage()),resp);
			return;			
		} catch (RepositoryException re) {
			DAVUtil.handleError(new DAVException(500,re.getMessage()),resp);
			return;
		}
		
	}

}
