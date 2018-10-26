package davserver.repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import davserver.DAVServer;
import davserver.DAVUtil;
import davserver.protocol.xml.ResourceType;

/**
 * Base class for a dav repository collection 
 * 
 * @author tkrieger
 *
 */
public abstract class Collection extends Resource {
	
	/**
	 * Collection resource type
	 */
	private ResourceType resourceType;
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param name
	 */
	public Collection(String name) {
		super(name);
		
		resourceType = new ResourceType();
		resourceType.addType(DAVServer.Namespace, "collection");
	}

	/***
	 * Query a collection child with name 
	 * 
	 * @param name
	 * @return the resource or null if no resource is behind the name
	 */
	public abstract Resource getChild(String name);
	
	/**
	 * Get a iterator above all resource childs
	 * @return
	 */
	public abstract Iterator<Resource> getChildIterator();
	
	/**
	 * Query content length of the get representation (to be done)
	 */
	public long getContentLength() {
		return 0;
	}
	
	/**
	 * Caclulates a given get request
	 */
	public InputStream getContent() throws IOException {
		return null;
	}
		
	/**
	 * get collection etag  
	 */
	public String getETag() {
		Iterator<Resource> iter = this.getChildIterator();
		StringBuffer b = new StringBuffer();
		
		while (iter.hasNext()) {
			Resource r = iter.next();
			b.append(r.getETag());
		}
		
		try {
			return DAVUtil.createHash(b.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return UUID.randomUUID().toString();
		}
	}
	
	/**
	 * Get the resource types supported
	 * @return
	 */
	public ResourceType getResourceTypes() {
		return resourceType;
	}
	
}
