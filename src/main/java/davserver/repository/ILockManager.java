package davserver.repository;

import davserver.repository.error.LockedException;

public interface ILockManager {

	LockEntry checkLocked(String ref);
	
	LockEntry registerLock(String ref) throws LockedException;

	
}
