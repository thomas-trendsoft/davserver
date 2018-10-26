package davserver.repository.cal;

import davserver.DAVServer;
import davserver.protocol.acl.Principal;
import davserver.protocol.xml.ResourceType;
import davserver.repository.Collection;
import ical4dav.caldav.resources.Calendar;

/**
 * Calendar resource implementation 
 * 
 * @author tkrieger
 *
 */
public abstract class VCalendar extends Collection {
	
	/**
	 * Calendar resource type
	 */
	private ResourceType resourceType;
	
	/**
	 * Internal representation
	 */
	private Calendar calendar;
	
	/**
	 * Calendar owner
	 */
	private Principal owner;
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param name
	 */
	public VCalendar(String name) {
		super(name);
		
		calendar = new Calendar();
		
		resourceType = new ResourceType();
		resourceType.addType(DAVServer.CalDAVNS, "calendar");
		
	}
	
	public Calendar getCalendar() {
		return calendar;
	}
	
	public Principal getOwner() {
		return owner;
	}

	public void setOwner(Principal owner) {
		this.owner = owner;
	}

	@Override 
	public ResourceType getResourceTypes() {
		return resourceType;
	}

}
