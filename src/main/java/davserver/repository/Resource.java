package davserver.repository;

import davserver.DAVUrl;

public abstract class Resource {
	
	/**
	 * Dynamic Href for External Referencing
	 */
	public DAVUrl dhref;
	
	private boolean writeable;
	
	public Resource() {
		writeable = true;
	}

	public abstract Property getProperty(PropertyRef ref);

	public DAVUrl getDhref() {
		return dhref;
	}

	public void setDhref(DAVUrl dhref) {
		this.dhref = dhref;
	}

	public boolean isWriteable() {
		return writeable;
	}
	
}
