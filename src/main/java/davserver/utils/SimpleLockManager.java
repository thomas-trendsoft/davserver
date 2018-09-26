package davserver.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import davserver.protocol.xml.SupportedLocks;
import davserver.repository.ILockManager;
import davserver.repository.LockEntry;

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
	private ConcurrentHashMap<String, HashMap<String,LockEntry>> locks;
	
	/**
	 * Supported Lock default property
	 */
	private SupportedLocks support;
	
	/**
	 * Defaultkonstruktor 
	 */
	public SimpleLockManager() {
		locks   = new ConcurrentHashMap<String, HashMap<String,LockEntry>>();
		support = new SupportedLocks();
	}
	
	/**
	 * Check specific token
	 * 
	 * @param ref
	 * @param token
	 * @return
	 */
	public synchronized LockEntry checkLocked(String ref,String token) {
		HashMap<String,LockEntry> rm = locks.get(ref);
		if (rm != null) {
			return rm.get(token);
		}
		return null;
	}
	
	/**
	 * Check resource ref for lock entry
	 */
	public HashMap<String,LockEntry> checkLocked(String ref) {		
		// check lock entries
		HashMap<String,LockEntry> le = locks.get(ref);
		
		// check list
		if (le != null) {
			// check timeout
			List<LockEntry> to = new LinkedList<>();
			Date now = new Date();
			for (LockEntry l : le.values()) {
				if (l.getTimeout() <= now.getTime()) {
					to.add(l);
				} 
			}
			// remove timeouts 
			for (LockEntry l : to) {
				le.remove(l.getToken());
			}
			locks.put(ref, le);
		}
		
		if (le == null || le.size() == 0) 
			return null;
		else
			return le;
	}
	
	public void updateLock(LockEntry le) {
		HashMap<String,LockEntry> list = locks.get(le.getRef());
		if (list != null) {
			list.put(le.getToken(), le);		
		}
		System.out.println(this);
	}
	
	public void removeLock(LockEntry le) {
		HashMap<String,LockEntry> list = locks.get(le.getRef());
		if (list != null) {
			list.remove(le.getToken());			
		}
		System.out.println(this);
	}
		
	public synchronized LockEntry registerLock(LockEntry request) {
		
		LockEntry le = new LockEntry(request.getRef(),request.getDepth(),request.getToken());
		le.setShared(request.isShared());
		le.setType(request.getType());
		le.getOwner().addAll(request.getOwner());
	
		System.out.println("register lock: " + request.getRef());
		HashMap<String,LockEntry> list = locks.get(le.getRef());
		if (list == null)
			list = new HashMap<>();
		
		list.put(request.getToken(), le);
		locks.put(request.getRef(), list);
		
		System.out.println(this);
		return le;
	}

	@Override
	public SupportedLocks getSupportedLocks() {
		return support;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (String r : locks.keySet()) {
			buf.append("res: " + r);
			HashMap<String,LockEntry> list = locks.get(r);
			for (LockEntry l : list.values()) {
				buf.append("  " + l.getRef() + " = " + l.getToken() + "\n");				
			}
		}
		return buf.toString();
	}

}
