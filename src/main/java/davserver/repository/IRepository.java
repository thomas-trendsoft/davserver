package davserver.repository;

import java.io.IOException;
import java.io.InputStream;

import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.RepositoryException;

/**
 * Repository Interface for own repository implementations 
 * 
 * @author tkrieger
 *
 */
public interface IRepository {
	
	/**
	 * Locate a specific resource by reference 
	 * 
	 * @param uri Resource reference
	 * @return the resource or null if not exists (better to throw NotFoundException)
	 * @throws NotFoundException
	 * @throws NotAllowedException
	 */
	public Resource locate(String uri) throws NotFoundException,NotAllowedException;
	
	/**
	 * Remove / Delete a given resource
	 * 
	 * @param uri Resource reference
	 * @throws NotFoundException
	 * @throws NotAllowedException
	 * @throws LockedException
	 */
	public void remove(String uri) throws NotFoundException,NotAllowedException,LockedException;
	
	/**
	 * Create a new collection on the given resource reference 
	 * 
	 * @param ref Resource reference
	 * @throws RepositoryException
	 */
	public void createCollection(String ref) throws RepositoryException;
	
	/**
	 * Create a new resource on the given resource reference 
	 * 
	 * @param ref
	 * @param data
	 * @throws RepositoryException
	 * @throws IOException
	 */
	public void createResource(String ref,InputStream data) throws RepositoryException,IOException;
	
	/**
	 * Check if the repository supports locking
	 * 
	 * @return true if the implementation supports locking, false otherwise
	 */
	public boolean supportLocks();
	
	/**
	 * Get the lock manager implementation of the repository 
	 * 
	 * @return
	 */
	public ILockManager getLockManager();

}
