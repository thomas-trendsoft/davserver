package davserver.protocol;

import org.apache.http.Header;
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
 * HTTP COPY Method implementation
 * 
 * @author tkrieger
 *
 */
public class DAVCopy {

	public void handleCopy(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url) {
		System.out.println("handle copy/move");

		try {
			// check destination header
			Header d = req.getFirstHeader("Destination");
			if (d == null) {
				DAVUtil.handleError(new DAVException(400,"destination header missing"), resp);
				return;
			}
			
			// check source resource
			if (url.getResref() == null) {
				DAVUtil.handleError(new DAVException(400,"no source reference"), resp);
				return;
			}
			Resource src = repos.locate(url.getResref());
			if (src == null) {
				DAVUtil.handleError(new DAVException(404,"source not found"), resp);
				return;				
			}
			
		} catch (NotAllowedException nae) {
			DAVUtil.handleError(new DAVException(403,nae.getMessage()), resp);
			return;
		} catch (NotFoundException nfe) {
			DAVUtil.handleError(new DAVException(404,nfe.getMessage()), resp);
			return;
		}
	}
	
}
