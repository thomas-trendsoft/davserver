package davserver.repository;

import java.util.HashMap;

import davserver.DAVException;
import davserver.protocol.xml.SupportedLocks;
import davserver.repository.error.LockedException;

/**
 * Lockmanger interface to implement LOCK Behaviour for your Repository
 * 
 * @author tkrieger
 *
 */
public interface ILockManager {

	/**
	 * Check a lock on a resource
	 * 
	 * @param ref Resource reference
	 * @param childs include child locks
	 * @return lock entry or null if no lock exists
	 */
	HashMap<String,LockEntry> checkLocked(String ref,boolean childs) throws DAVException;
	

	/**
	 * Register a new lock or update an existing 
	 * 
	 * @param ref Resource reference
	 * @return lock entry 
	 * @throws LockedException
	 */
	LockEntry registerLock(LockEntry request);

	/**
	 * update locks if not in memory only
	 */
	void updateLock(LockEntry lock);
	
	/**
	 * remove a lock from the store
	 * 
	 * @param lock
	 */
	void removeLock(LockEntry lock);
	
	/**
	 * Get the supported lock types
	 * 
	 * @return
	 */
	SupportedLocks getSupportedLocks();
	
}
