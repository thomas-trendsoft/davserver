package davserver.repository;

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
	 * @return lock entry or null if no lock exists
	 */
	LockEntry checkLocked(String ref);
	
	/**
	 * Register a new lock or update an existing 
	 * 
	 * @param ref Resource reference
	 * @return lock entry 
	 * @throws LockedException
	 */
	LockEntry registerLock(String ref) throws LockedException;

	
}
