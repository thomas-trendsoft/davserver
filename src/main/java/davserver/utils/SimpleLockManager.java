package davserver.utils;

import java.util.HashMap;

import davserver.repository.ILockManager;
import davserver.repository.LockEntry;
import davserver.repository.error.LockedException;

public class SimpleLockManager implements ILockManager {
	
	private HashMap<String,LockEntry> locks;
	
	public SimpleLockManager() {
		locks = new HashMap<String,LockEntry>();
	}
	
	public LockEntry checkLocked(String ref) {
		return locks.get(ref);
	}
	
	public synchronized LockEntry registerLock(String ref) throws LockedException {
		
		if (checkLocked(ref) != null) {
			throw new LockedException(ref + " is locked");
		}
		
		LockEntry le = new LockEntry(ref);
		locks.put(ref, le);
		return le;
	}

}
