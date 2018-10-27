package davserver.repository.cal.simple;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import davserver.DAVServer;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.cal.VCalendar;
import davserver.repository.cal.properties.SupportedCalendarCompSet;
import davserver.repository.cal.resource.CalDAVResource;
import davserver.repository.properties.SupportedReportSet;
import ical4dav.caldav.resources.Timezone;
import ical4dav.parser.TokenMap;
import ical4dav.properties.StringProperty;

/**
 * Memory managed calendar implementation 
 * 
 * @author tkrieger
 *
 */
public class SimpleCalendar extends VCalendar {

	/**
	 * Memory childs
	 */
	private HashMap<String,Resource> childs;

	/**
	 * property map
	 */
	private HashMap<String,Property> properties;
	
	/**
	 * creation date
	 */
	private Date created;
	
	/**
	 * last modified
	 */
	private Date lm;	
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param name
	 */
	public SimpleCalendar(String name) {
		super(name);
		
		childs     = new HashMap<String,Resource>();
		properties = new HashMap<String,Property>();
		created    = new Date();
		lm         = new Date();
		
		// minimal reports
		List<Property> r = new LinkedList<>();
		r.add(new Property(DAVServer.Namespace, "expand-property", null));
		r.add(new Property(DAVServer.CalDAVNS,"calendar-multiget", null));
		
		// set the timezone
		Timezone tz = new Timezone();
		tz.setTzId(new StringProperty(TokenMap.TZID, "Europe/Berlin", null));
		this.getCalendar().setTimezone(tz);
		
		// supported components
		this.setProperty(new SupportedCalendarCompSet(Arrays.asList("VEVENT")));
		this.setProperty(new SupportedReportSet(r));
		this.setProperty(new Property(DAVServer.Namespace, "owner", "admin"));
		
	}
	
	@Override
	public String getContentType() {
		return "text/calendar";
	}

	@Override
	public Resource getChild(String name) {
		return childs.get(name);
	}
	
	@Override
	public void addChild(String name,CalDAVResource child) {
		this.childs.put(name, (Resource)child);
	}

	@Override
	public Iterator<Resource> getChildIterator() {
		return childs.values().iterator();
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		return properties.get(ref.getNs() + ref.getName());
	}

	@Override
	public void remProperty(PropertyRef ref) {
		properties.remove(ref.getNs() + ref.getName());
	}

	@Override
	public void setProperty(Property p) {
		properties.put(p.getNamespace() + p.getName(), p);
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return properties.values().iterator();
	}

	@Override
	public Date getCreationDate() {
		return created;
	}

	@Override
	public Date getLastmodified() {
		return lm;
	}


}
