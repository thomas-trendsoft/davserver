package davserver.protocol;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.Collection;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

/**
 * HTTP GET Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVGet {

	/**
	 * Defaultkonstruktor 
	 */
	public DAVGet() {
	}
	
	/**
	 * Handle the HTTP Get Method
	 * 
	 * @param req HTTP Request
	 * @param response HTTP Response
	 * @param repos Repository
	 * @param head Flag for head request without body
	 */
	public void handleGet(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url,boolean head) {
		System.out.println("handle get");
		
		// check url and repos
		if (url == null || repos == null) {
			DAVUtil.handleError(new DAVException(404,"not found"), resp);
			return;
		}
		
		try {
			Resource r = repos.locate(url.getResref());
			if (r != null) {
				resp.addHeader("ETag",r.getETag());
				if (r instanceof Collection) {
					DAVUtil.handleError(new DAVException(415,"not implemented"), resp);
					return;
				} else if (!head) {
					InputStream data;
					if ((data = r.getContent()) != null) {
						resp.setEntity(new InputStreamEntity(data));
					}
				}
			} else {
				DAVUtil.handleError(new DAVException(404,"not found"), resp);
				return;
			} 
		} catch (NotAllowedException nae) {
			DAVUtil.handleError(new DAVException(403,"not allowed"), resp);
			return;
		} catch (NotFoundException e) {
			DAVUtil.handleError(new DAVException(404,"not found"), resp);
			return;
		} catch (IOException e) {
			DAVUtil.handleError(new DAVException(500,e.getMessage()), resp);
			return;			
		}
	}
	

}
