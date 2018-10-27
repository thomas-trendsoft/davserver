package davserver.repository.cal;

import java.util.List;

import davserver.DAVServer;
import davserver.protocol.acl.Principal;
import davserver.repository.Collection;
import davserver.repository.cal.resource.CalDAVResource;
import davserver.repository.properties.ResourceType;
import ical4dav.caldav.resources.Calendar;
import ical4dav.properties.iCalComponent;

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
	
	public abstract void addChild(String name,CalDAVResource comp);
	
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
