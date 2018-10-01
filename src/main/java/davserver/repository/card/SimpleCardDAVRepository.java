package davserver.repository.card;

import java.io.IOException;
import java.io.InputStream;

import davserver.DAVServer;
import davserver.protocol.auth.BasicAuthProvider;
import davserver.protocol.auth.IAuthenticationProvider;
import davserver.repository.Collection;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.ResourceExistsException;
import davserver.repository.simple.SimpleRepository;

/**
 * Simple CardDAV Repository Implementation
 * 
 * @author tkrieger
 *
 */
public class SimpleCardDAVRepository extends SimpleRepository {
	
	/**
	 * auth provider 
	 */
	private BasicAuthProvider authProvider;
	
	/**
	 * Defaultkonstruktor 
	 */
	public SimpleCardDAVRepository() {
		authProvider = new BasicAuthProvider(null);
	}

	@Override
	public Collection createCollection(String ref)
			throws NotAllowedException, ResourceExistsException, ConflictException {
		return null;
	}

	@Override
	public Resource createResource(String ref, InputStream data)
			throws NotAllowedException, ConflictException, IOException {
		return null;
	}

	@Override
	public int getProtocol() {
		return DAVServer.PROT_CARDDAV;
	}

	@Override
	public boolean needsAuth() {
		return true;
	}

	@Override
	public IAuthenticationProvider getAuthProvider() {
		return authProvider;
	}

}
