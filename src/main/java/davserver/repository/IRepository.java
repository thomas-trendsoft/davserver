package davserver.repository;

import java.io.IOException;
import java.io.InputStream;

import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.RepositoryException;

public interface IRepository {
	
	public Resource locate(String uri) throws NotFoundException,NotAllowedException;
	
	public void remove(String uri) throws NotFoundException,NotAllowedException,LockedException;
	
	public void createCollection(String ref) throws RepositoryException;
	
	public void createResource(String ref,InputStream data) throws RepositoryException,IOException;
	
	public boolean supportLocks();
	
	public ILockManager getLockManager();

}
