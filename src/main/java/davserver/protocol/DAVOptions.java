package davserver.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
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
public class DAVOptions extends DAVRequest {

	/**
	 * Handle OPTIONS method
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param durl DAV Url
	 */
	public void handle(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl durl) throws DAVException, NotFoundException,NotAllowedException {
		Resource r = null;
		
		System.out.println("handle options request: " + req.getRequestLine().getUri());
		
		// check if a given resource is addressed if not root
		if (durl.getResref() != null && durl.getResref().length() > 1) {
			r = repos.locate(durl.getResref());
		}
		
		// add dav supported versions
		resp.addHeader("DAV", "1,2");
		
		// give server or resource type info
		if (r == null) {
			resp.addHeader("Allow","GET,POST,OPTIONS,HEAD,MKCOL,PUT,PROPFIND,PROPPATCH,DELETE,MOVE,COPY,REPORT"+ (repos.supportLocks() ? ",LOCK,UNLOCK" : ""));			
		} else {
			resp.addHeader("Access-Control-Allow-Methods","PUT,POST,GET,OPTIONS,REPORT" + (repos.supportLocks() ? ",LOCK,UNLOCK" : ""));
		} 
	}
	
}
