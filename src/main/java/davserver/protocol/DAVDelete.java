package davserver.protocol;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.repository.IRepository;
import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

/**
 * DELETE Implementation Class
 * 
 * @author tkrieger
 *
 */
public class DAVDelete extends DAVRequest {

	/**
	 * Handle the HTTP Method
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param durl DAV Url
	 */
	public void handle(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl durl) throws DAVException,NotFoundException,NotAllowedException {

		if (durl == null || durl.getResref() == null) {
			throw new DAVException(404,"not found");
		}
		
		try {
			// check precondition
			DAVRequest.checkLock(req, repos, durl,true);
			
			repos.remove(durl.getResref());
			resp.setStatusCode(204);
		} catch (LockedException le) {
			throw new DAVException(423,le.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		}
		
	}

}
