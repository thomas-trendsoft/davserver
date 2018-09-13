package davserver.repository;

import java.util.HashSet;

public class LockEntry {
	
	private String ref;
	
	private HashSet<String> owner;
	
	public LockEntry(String ref) {
		this.ref   = ref;
		this.owner = new HashSet<String>();
	}

	public String getRef() {
		return ref;
	}

	public HashSet<String> getOwner() {
		return owner;
	}
	
}
