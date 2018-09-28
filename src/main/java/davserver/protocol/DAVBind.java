package davserver.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.repository.IRepository;

/**
 * BIND Implementation 
 * 
 * @author tkrieger
 *
 */
public class DAVBind extends DAVRequest {

	/**
	 * Handle bind requests
	 * 
	 * @param req
	 * @param resp
	 * @param repos
	 * @param url
	 * @throws DAVException 
	 */
	public void handle(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url) throws DAVException {
		throw new DAVException(400,"not supported now");
	}
}
