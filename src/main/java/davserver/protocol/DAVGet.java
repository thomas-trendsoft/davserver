package davserver.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

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

	private boolean debug;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVGet() {
		this.debug = true;
	}
	
	/**
	 * Handle the HTTP Get Method
	 * 
	 * @param req HTTP Request
	 * @param response HTTP Response
	 * @param repos Repository
	 */
	public void handleGet(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url) {
		System.out.println("handle get");
		
		// check url and repos
		if (url == null || repos == null) {
			DAVUtil.handleError(new DAVException(404,"not found"), resp);
			return;
		}
		
		try {
			Resource r = repos.locate(url.getResref());
			if (r instanceof Collection) {
				DAVUtil.handleError(new DAVException(415,"not implemented"), resp);
				return;				
			} else if (r == null) {
				DAVUtil.handleError(new DAVException(404,"not found"), resp);
				return;				
			} else {
				InputStream data;
				if ((data = r.getContent()) != null) {
					resp.setEntity(new InputStreamEntity(data));
				}
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
