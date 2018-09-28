package davserver.protocol;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUrl;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.RepositoryException;
import davserver.repository.error.ResourceExistsException;

/**
 * HTTP PUT Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVPut extends DAVRequest {

	
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
	public void handle(HttpRequest breq,HttpResponse resp,IRepository repos,DAVUrl url) throws DAVException {
		HttpEntityEnclosingRequest req;
		
		// check request
		if (!(breq instanceof HttpEntityEnclosingRequest)) {
			throw new DAVException(400,"no body");
		}
		req = (HttpEntityEnclosingRequest)breq;

		if (url.getResref() == null) {
			throw new DAVException(400,"bad request");
		}
		
		try {
			// check preconditions
			DAVRequest.checkLock(req, repos, url);
			
			// create resource
			Resource r = repos.createResource(url.getResref(),req.getEntity().getContent());
			
			// strong etag return for carddav 
			if (repos.getProtocol() == DAVServer.PROT_CARDDAV && r != null) {
				resp.addHeader("ETag", r.getETag());
			}
			
			resp.setStatusCode(201);
			
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
