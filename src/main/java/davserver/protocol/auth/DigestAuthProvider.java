package davserver.protocol.auth;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class DigestAuthProvider implements IAuthenticationProvider {

	@Override
	public boolean authRequest(HttpRequest req,Session session) {
		return false;
	}

	@Override
	public void rejectedResponse(HttpResponse resp) {
	}

}
