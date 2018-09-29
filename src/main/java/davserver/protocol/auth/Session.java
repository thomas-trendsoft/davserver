package davserver.protocol.auth;

import davserver.protocol.acl.Principal;

/**
 * http session 
 * 
 * @author tkrieger
 *
 */
public class Session {
	
	/**
	 * session id
	 */
	private String id;
	
	/**
	 * authed principal or null
	 */
	private Principal principal;

	/**
	 * Defaultconstructor 
	 * 
	 * @param id
	 */
	public Session(String id) {
		this.id        = id;
		this.principal = null;
	}
	
	/**
	 * check session auth
	 * @return
	 */
	public boolean isAuth() {
		return principal != null;
	}
	
	/**
	 * get session id
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

}
