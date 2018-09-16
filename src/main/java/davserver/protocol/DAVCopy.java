package davserver.protocol;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.Collection;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.ResourceExistsException;

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
			Header d  = req.getFirstHeader("Destination");
			Header ow = req.getFirstHeader("Overwrite");
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
			
			// check target resource TODO check prefix management for resources
			System.out.println(d.getValue());
			// TODO check target host
			URI turi = new URI(d.getValue());
			DAVUrl turl = new DAVUrl(turi.getPath(),"");
			if (turl.getResref() == null) {
				DAVUtil.handleError(new DAVException(400,"no readable target reference"), resp);
				return;
			}	
			
			try {
				Resource target = repos.locate(turl.getResref());				
				if (target != null && ow.getValue().compareTo("F")==0) {
					DAVUtil.handleError(new DAVException(412,"precondition failed"), resp);
					return;
				}
			} catch (NotFoundException nfe) {
				// exception ok 
			}
			
			
			// Check resource type of source element 
			if (src instanceof Collection) {
				DAVUtil.handleError(new DAVException(500,"not implemented"), resp);
				return;
			} else {
				repos.createResource(turl.getResref(), src.getContent());
				resp.setStatusCode(201);
			}
			
		} catch (NotAllowedException nae) {
			DAVUtil.handleError(new DAVException(403,nae.getMessage()), resp);
			return;
		} catch (NotFoundException nfe) {
			nfe.printStackTrace();
			DAVUtil.handleError(new DAVException(404,nfe.getMessage()), resp);
			return;
		} catch (IOException ioe) {
			DAVUtil.handleError(new DAVException(500, ioe.getMessage()),resp);
		} catch (ConflictException ce) {
			// TODO Check RFC Handling
		} catch (ResourceExistsException ree) {
			// TODO Check RFC Handling
		} catch (URISyntaxException use) {
			DAVUtil.handleError(new DAVException(400,"bad location"), resp);
		}
	}
	
}
