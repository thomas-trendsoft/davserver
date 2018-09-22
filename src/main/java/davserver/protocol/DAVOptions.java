package davserver.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

/**
 * HTTP OPTIONS Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVOptions {

	/**
	 * Handle OPTIONS method
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param durl DAV Url
	 */
	public void handleOptions(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl durl) {
		Resource r = null;
		
		System.out.println("handle options request: " + req.getRequestLine().getUri());
		
		// check if a given resource is addressed if not root
		if (durl.getResref() != null && durl.getResref().length() > 1) {
			try {
				r = repos.locate(durl.getResref());
			} catch (NotFoundException e) {
				DAVUtil.handleError(new DAVException(404,"resource not found"), resp);
				return;
			} catch (NotAllowedException e) {
				DAVUtil.handleError(new DAVException(403,"not allowed"), resp);
				return;
			}
		}
		
		// add dav supported versions
		resp.addHeader("DAV", "1,2");
		
		// give server or resource type info
		if (r == null) {
			resp.addHeader("Allow","GET, POST, OPTIONS, HEAD, MKCOL, PUT, PROPFIND, PROPPATCH, DELETE, MOVE, COPY" + (repos.supportLocks() ? ", LOCK, UNLOCK" : ""));			
		} else {
			resp.addHeader("Access-Control-Allow-Methods","PUT, POST, GET, OPTIONS" + (repos.supportLocks() ? ", LOCK, UNLOCK" : ""));
		} 
	}
	
}
