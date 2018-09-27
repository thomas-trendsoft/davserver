package davserver.repository.cal;

import java.io.IOException;
import java.io.InputStream;

import davserver.repository.Collection;
import davserver.repository.ILockManager;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.ResourceExistsException;
import davserver.utils.SimpleLockManager;

public class SimpleCalDAVRepository implements IRepository {

	/**
	 * Locking manager
	 */
	private SimpleLockManager lmanager;
	
	public SimpleCalDAVRepository() {
		lmanager = new SimpleLockManager();
	}
	
	@Override
	public Resource locate(String uri) throws NotFoundException, NotAllowedException {
		return null;
	}

	@Override
	public void remove(String uri) throws NotFoundException, NotAllowedException, LockedException {
	}

	@Override
	public Collection createCollection(String ref)
			throws NotAllowedException, ResourceExistsException, ConflictException {
		return null;
	}

	@Override
	public Resource createResource(String ref, InputStream data)
			throws NotAllowedException, ConflictException, ResourceExistsException, NotFoundException, IOException {
		return null;
	}

	@Override
	public boolean supportLocks() {
		return true;
	}

	@Override
	public ILockManager getLockManager() {
		return lmanager;
	}

}
