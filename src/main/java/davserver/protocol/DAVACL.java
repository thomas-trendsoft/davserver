package davserver.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.repository.IRepository;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

public class DAVACL extends DAVRequest {

	@Override
	public void handle(HttpRequest req, HttpResponse resp, IRepository repos, DAVUrl url)
			throws DAVException, NotFoundException, NotAllowedException {
		
	}

}
