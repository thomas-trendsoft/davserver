package davserver.protocol.auth;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * Interface to manage user account auth 
 * 
 * @author tkrieger
 *
 */
public interface IAuthenticationProvider {

	/**
	 * try to auth the current request 
	 * 
	 * @param req
	 * @param session
	 * @return
	 */
	boolean authRequest(HttpRequest req,Session session);
	
	/**
	 * ability to add auth protocol elements to a response 
	 * 
	 * @param resp
	 */
	void rejectedResponse(HttpRequest req,HttpResponse resp);
	
}
