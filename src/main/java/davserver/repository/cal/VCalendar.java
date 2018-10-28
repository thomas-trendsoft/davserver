package davserver.repository.cal;

import java.util.UUID;

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
public abstract class VCalendar extends Collection implements CalDAVResource {
	
	/**
	 * Calendar resource type
	 */
	private ResourceType resourceType;
	
	/**
	 * Internal representation
	 */
	private Calendar calendar;
	
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
	
	@Override
	public String getETag() {
		return UUID.randomUUID().toString();
	}
	
	public Calendar getCalendar() {
		return calendar;
	}
	
	@Override 
	public ResourceType getResourceTypes() {
		return resourceType;
	}

	@Override
	public iCalComponent getComponent() {
		return calendar;
	}

}
