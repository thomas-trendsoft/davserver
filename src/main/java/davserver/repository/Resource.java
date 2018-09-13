package davserver.repository;

import java.io.IOException;
import java.io.InputStream;

import davserver.DAVUrl;

public abstract class Resource {
	
	/**
	 * Dynamic Href for External Referencing
	 */
	public DAVUrl dhref;
	
	/**
	 * Flag if the resource is writeable
	 */
	private boolean writeable;
	
	/**
	 * Defaultkonstruktor 
	 */
	public Resource() {
		writeable = true;
	}

	/**
	 * Abstract Method to get a specific property of the resource
	 * 
	 * @param ref
	 * @return
	 */
	public abstract Property getProperty(PropertyRef ref);

	/**
	 * Check writeable status 
	 * 
	 * @return
	 */
	public boolean isWriteable() {
		return writeable;
	}
	
	/**
	 * Get Resource Content Length 
	 * 
	 * @return Byte count of the content stream
	 */
	public abstract int getContentLength();
	
	/**
	 * Get the resource data as input stream
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getContent() throws IOException;
	
}
