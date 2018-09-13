package davserver.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

public class DAVDelete {

	public void handleDelete(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl durl) {
		System.out.println("handle delete");
		Resource r = null;
		
		// check resource to be deleted
		if (durl.getResref() == null) {
			try {
				r = repos.locate(durl.getResref());
			} catch (NotFoundException e) {
				DAVUtil.handleError(new DAVException(404,"not found"), resp);
				return;
			} catch (NotAllowedException e) {
				DAVUtil.handleError(new DAVException(403,"not allowed"), resp);
				return;
			}
		}
		
		if (r == null) {
			DAVUtil.handleError(new DAVException(404,"not found"), resp);
			return;
		}
		
		try {
			repos.remove(r);
			resp.setStatusCode(204);
		} catch (LockedException le) {
			DAVUtil.handleError(new DAVException(423,le.getMessage()),resp);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			DAVUtil.handleError(new DAVException(500,e.getMessage()),resp);
			return;
		}
		
	}

}
