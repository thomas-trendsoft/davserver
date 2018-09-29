package davserver.protocol.auth;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * basic default auth provider (no acl given owner = all and other = read)
 * 
 * @author tkrieger
 *
 */
public class BasicAuthProvider implements IAuthenticationProvider {
	
	/**
	 * auth header regex
	 */
	private static final Pattern PAuthHeader = Pattern.compile("Basic (?<creds>[A-Za-z0-9]+)");

	/**
	 * base 64 decoder
	 */
	private Decoder bdecode;
	
	/**
	 * Defaultconstructor 
	 */
	public BasicAuthProvider(SessionStore sessions) {
		bdecode      = Base64.getDecoder();
	}
	
	@Override
	public boolean authRequest(HttpRequest req,Session session) {
		System.out.println("try to auth request on basic");
		
		// check auth header
		Header auth = req.getFirstHeader("Authorization");
		if (auth == null)
			return false;
		
		// parse credentials
		Matcher m = PAuthHeader.matcher(auth.getValue());
		if (m.find()) {
			String creds = m.group("creds");
			System.out.println("creds: " + creds);
			if (creds == null)
				return false;
			try {
				String dcreds = new String(bdecode.decode(creds),"utf-8");
				System.out.println("dcreds: " + dcreds);
				String[] acreds = dcreds.split(":");
				if (acreds == null || acreds.length != 2) {
					return false;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
		
		return false;
	}

	@Override
	public void rejectedResponse(HttpResponse resp) {
		resp.addHeader("WWW-Authenticate", "Basic realm=\"WebDAV Server\"");
	}
	
}
