package davserver.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import org.w3c.dom.Document;

import davserver.DAVUrl;
import davserver.protocol.acl.ACLNode;
import davserver.repository.error.NotAllowedException;
import davserver.repository.properties.ResourceType;

public abstract class Resource extends ACLNode {
	
	/**
	 * Dynamic Href for External Referencing
	 */
	public DAVUrl dhref;
	
	/**
	 * Flag if the resource is writeable
	 */
	private boolean writeable;
	
	/**
	 * Name of the resource
	 */
	private String name;
	
	/**
	 * Defaultkonstruktor 
	 */
	public Resource(String name) {
		writeable = true;
		this.name = name;
	}

	/**
	 * Abstract Method to get a specific property of the resource
	 * 
	 * @param ref
	 * @return
	 */
	public abstract Property getProperty(PropertyRef ref);
	
	/**
	 * Removes an property of the resource
	 */
	public abstract void remProperty(PropertyRef ref);
	
	/**
	 * Set a property for the resource 
	 * 
	 * @param p
	 */
	public abstract void setProperty(Property p) throws NotAllowedException;
	
	/**
	 * Get a resource property iterator
	 * 
	 * @return
	 */
	public abstract Iterator<Property> getPropertyIterator();
	
	/**
	 * Get the current 
	 */
	public abstract String getETag();

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
	public abstract long getContentLength();
	
	/**
	 * Creation date of resource
	 * 
	 * @return
	 */
	public abstract Date getCreationDate();
	
	/**
	 * Last modified date of resource
	 * 
	 * @return
	 */
	public abstract Date getLastmodified();
	
	/**
	 * Content type accessor with default as binary data
	 * @return
	 */
	public String getContentType() {
		return "application/octet-stream";
	}
	
	/**
	 * Get the resource data as input stream
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getContent() throws IOException;

	/**
	 * Get the resource name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the resource types supported
	 * @return
	 */
	public abstract ResourceType getResourceTypes();
	
}
