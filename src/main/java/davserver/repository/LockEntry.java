package davserver.repository;

import java.util.HashSet;

/**
 * LockEntry for a resource 
 * 
 * @author tkrieger
 *
 */
public class LockEntry {
	
	/**
	 * Resource reference
	 */
	private String ref;
	
	/**
	 * Lock owner list
	 */
	private HashSet<String> owner;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param ref
	 */
	public LockEntry(String ref) {
		this.ref   = ref;
		this.owner = new HashSet<String>();
	}

	/**
	 * Get the resource reference
	 * 
	 * @return
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * Get the lock owner list
	 * 
	 * @return
	 */
	public HashSet<String> getOwner() {
		return owner;
	}
	
}
