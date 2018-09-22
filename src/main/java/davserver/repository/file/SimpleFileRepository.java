package davserver.repository.file;

import java.io.File;
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

public class SimpleFileRepository implements IRepository {

	/**
	 * Root Directory
	 */
	private File root;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param path
	 */
	public SimpleFileRepository(String path) {
		root = new File(path);
	}
	
	@Override
	public Resource locate(String uri) throws NotFoundException, NotAllowedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(String uri) throws NotFoundException, NotAllowedException, LockedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection createCollection(String ref)
			throws NotAllowedException, ResourceExistsException, ConflictException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource createResource(String ref, InputStream data)
			throws NotAllowedException, ConflictException, ResourceExistsException, NotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportLocks() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ILockManager getLockManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
