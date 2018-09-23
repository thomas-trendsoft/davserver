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
		System.out.println("check lock: " + ref);
		return locks.get(ref);
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

}
