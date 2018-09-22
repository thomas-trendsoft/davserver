package davserver.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;

/**
 * BIND Implementation 
 * 
 * @author tkrieger
 *
 */
public class DAVBind {

	/**
	 * Handle bind requests
	 * 
	 * @param req
	 * @param resp
	 * @param repos
	 * @param url
	 */
	public void handleBind(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url) {
		DAVUtil.handleError(new DAVException(400,"not supported now"), resp);
		return;
	}
}
