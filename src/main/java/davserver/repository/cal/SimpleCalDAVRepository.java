package davserver.repository.cal;

import davserver.DAVServer;
import davserver.protocol.auth.BasicAuthProvider;
import davserver.protocol.auth.IAuthenticationProvider;
import davserver.repository.simple.SimpleRepository;

/**
 * CalDAV repository sample implementation 
 * 
 * @author tkrieger
 *
 */
public class SimpleCalDAVRepository extends SimpleRepository {

	/**
	 * Basic auth provider
	 */
	private BasicAuthProvider authProvider;
	
	/**
	 * Defaultconstructor 
	 */
	public SimpleCalDAVRepository() {
		super();
		
		authProvider = new BasicAuthProvider();
	}
	
	@Override
	public int getProtocol() {
		return DAVServer.PROT_CARDDAV;
	}
	
	@Override
	public IAuthenticationProvider getAuthProvider() {
		return authProvider;
	}
	
	@Override
	public boolean needsAuth() {
		return true;
	}
	
}
