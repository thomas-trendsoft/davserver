package davserver.repository.cal;

import davserver.DAVServer;
import davserver.protocol.xml.ResourceType;
import davserver.repository.Collection;

/**
 * Calendar resource implementation 
 * 
 * @author tkrieger
 *
 */
public abstract class Calendar extends Collection {
	
	/**
	 * Calendar resource type
	 */
	private ResourceType resourceType;

	/**
	 * Defaultconstructor 
	 * 
	 * @param name
	 */
	public Calendar(String name) {
		super(name);
		
		resourceType = new ResourceType();
		resourceType.addType(DAVServer.CalDAVNS, "calendar");
	}
	
	@Override 
	public ResourceType getResourceTypes() {
		return resourceType;
	}

}
