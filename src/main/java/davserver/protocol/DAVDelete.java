package davserver.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
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
public class DAVDelete {

	/**
	 * Handle the HTTP Method
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param durl DAV Url
	 */
	public void handleDelete(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl durl) {
		System.out.println("handle delete");

		if (durl == null || durl.getResref() == null) {
			DAVUtil.handleError(new DAVException(404,"not found"), resp);
			return;
		}
		
		try {
			repos.remove(durl.getResref());
			resp.setStatusCode(204);
		} catch (LockedException le) {
			DAVUtil.handleError(new DAVException(423,le.getMessage()),resp);
			return;
		} catch (NotFoundException e) {
			DAVUtil.handleError(new DAVException(404,e.getMessage()),resp);
			return;
		} catch (NotAllowedException e) {
			DAVUtil.handleError(new DAVException(403,e.getMessage()),resp);
			return;			
		}
		
	}

}
