package davserver.protocol.auth;

import davserver.protocol.acl.Principal;

/**
 * Interface for credentials backend
 * 
 * @author tkrieger
 *
 */
public interface ICredentialStore {
	
	/**
	 * check auth and give related principal or null if no auth
	 * 
	 * @param username
	 * @param secret
	 * @return
	 */
	Principal checkAuth(String username,String secret);

}
