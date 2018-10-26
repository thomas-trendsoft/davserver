package davserver.protocol;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
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
public class DAVMkCol extends DAVRequest {
	
	/**
	 * Make Collection Implementation
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param url DAV Url
	 */
	public void handle(HttpRequest breq,HttpResponse resp,IRepository repos,DAVUrl url) throws DAVException,NotAllowedException {
		Resource r = null;
		HttpEntityEnclosingRequest req;
		
		// check request
		if (!(breq instanceof HttpEntityEnclosingRequest)) {
			throw new DAVException(400,"no body");
		}
		req = (HttpEntityEnclosingRequest)breq;

		// check precondition
		DAVRequest.checkLock(req, repos, url,false);

		// check if a body is given
		if (req.getEntity().getContentLength() > 0) {
			throw new DAVException(415,"No supported body mkcol");
		}
		
		// Check if a resource exits add the location
		if (url.getResref() == null) {
			throw new DAVException(400,"bad request");
		}
		
		// locate resource to check is not existing
		try {
			r = repos.locate(url.getResref());
		} catch (NotFoundException e) {
			r = null;
		} 
		
		if (r != null) {
			resp.setStatusCode(405);
			return;
		}
		
		// create new collection
		try {
			repos.createCollection(url.getResref());
			resp.setStatusCode(201);
		} catch (ConflictException ce) {
			throw new DAVException(409, ce.getMessage());
		} catch (ResourceExistsException ee) {
			throw new DAVException(405, ee.getMessage());
		} catch (RepositoryException re) {
			throw new DAVException(500,re.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());			
		}
		
	}

}
