package davserver.utils;

import java.util.Date;
import java.util.HashMap;

import davserver.protocol.xml.SupportedLocks;
import davserver.repository.ILockManager;
import davserver.repository.LockEntry;
import davserver.repository.error.LockedException;

/**
 * Simple Lock Manager implementation 
 * 
 * @author tkrieger
 *
 */
public class SimpleLockManager implements ILockManager {
	
	/**
	 * Memory lock store
	 */
	private HashMap<String,LockEntry> locks;
	
	/**
	 * Supported Lock default property
	 */
	private SupportedLocks support;
	
	/**
	 * Defaultkonstruktor 
	 */
	public SimpleLockManager() {
		locks   = new HashMap<String,LockEntry>();
		support = new SupportedLocks();
	}
	
	/**
	 * Check resource ref for lock entry
	 */
	public LockEntry checkLocked(String ref) {
		System.out.println("check lock: " + ref);
		LockEntry le = locks.get(ref);
		if (le != null) {
			// check timeout
			Date now = new Date();
			if (le.getTimeout() <= now.getTime()) {
				locks.remove(ref);
				return null;
			}
		}
		return le;
	}
	
	public void updateLock(LockEntry le) {
		locks.put(le.getRef(), le);
	}
	
	public void removeLock(LockEntry le) {
		locks.remove(le.getRef());
	}
	
	public synchronized LockEntry registerLock(LockEntry request) throws LockedException {
		
		if (checkLocked(request.getRef()) != null) {
			throw new LockedException(request.getRef() + " is locked");
		}
		
		LockEntry le = new LockEntry(request.getRef(),request.getDepth(),request.getToken());
		le.setShared(request.isShared());
		le.setType(request.getType());
		le.getOwner().addAll(request.getOwner());
	
		System.out.println("register lock: " + request.getRef());
		locks.put(request.getRef(), le);
		
		return le;
	}

	@Override
	public SupportedLocks getSupportedLocks() {
		return support;
	}

}
