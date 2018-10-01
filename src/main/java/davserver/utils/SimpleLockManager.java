package davserver.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import davserver.DAVException;
import davserver.DAVUtil;
import davserver.protocol.xml.SupportedLocks;
import davserver.repository.ILockManager;
import davserver.repository.LockEntry;
import davserver.repository.error.NotAllowedException;

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
	 * Check resource ref for lock entry
	 * @throws DAVException 
	 */
	public HashMap<String,LockEntry> checkLocked(String ref,boolean childs) throws DAVException {
		HashMap<String,LockEntry> result = new HashMap<>();
		
		// check direct lock entries
		HashMap<String,LockEntry> le = locks.get(ref);
		if (le != null) {
			result.putAll(le);
		}
		
		// check child locks if needed
		if (childs) {
			System.out.println("check child locks");
			for (String k : locks.keySet()) {
				if (k.startsWith(ref)) {
					System.out.println("add locks: " + k);
					result.putAll(locks.get(k));
				}
			}			
		}
		
		// check parent locks
		try {
			List<String> path = DAVUtil.getPathComps(ref);
			String cp = "/";
			for (int i=0;i<path.size()-1;i++) {
				String p = path.get(i);
				cp += p + "/";
				System.out.println("check parent lock: " + cp);
				le = locks.get(cp);
				if (le != null) {
					for (LockEntry l : le.values()) {
						if (l.getDepth() > 0) {
							result.put(l.getToken(), l);
						}
					}
				}
			} 
		} catch (NotAllowedException e) {
			throw new DAVException(405, "bad path");
		}

		// check list
		if (result != null) {
			// check timeout
			List<LockEntry> to = new LinkedList<>();
			Date now = new Date();
			for (LockEntry l : result.values()) {
				if (l.getTimeout() <= now.getTime()) {
					to.add(l);
				} 
			}
			// remove timeouts 
			for (LockEntry l : to) {
				this.removeLock(l);
			}
		}
		
		if (result == null || result.size() == 0) 
			return null;
		else
			return result;
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
