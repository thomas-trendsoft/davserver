package davserver.repository.cal.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import davserver.DAVUtil;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.error.NotAllowedException;
import davserver.repository.properties.ResourceType;
import ical4dav.caldav.resources.Event;
import ical4dav.properties.iCalComponent;

/**
 * CalDAV Mapping class from iCal Resource 
 * 
 * @author tkrieger
 *
 */
public class VEvent extends CalDAVResource {

	/**
	 * iCal Event
	 */
	private Event event;
	
	/**
	 * resource type
	 */
	private ResourceType resourceType;
	
	/**
	 * default constructor 
	 * 
	 * @param name
	 * @param event
	 */
	public VEvent(String name,Event event) {
		super(name);
		
		this.event        = event;
		this.resourceType = new ResourceType();
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		return null;
	}

	@Override
	public void remProperty(PropertyRef ref) {
	}

	@Override
	public void setProperty(Property p) throws NotAllowedException {
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return null;
	}

	@Override
	public String getETag() {
		try {
			return DAVUtil.createHash(event.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return UUID.randomUUID().toString();
		}
	}

	@Override
	public long getContentLength() {
		return event.toString().length();
	}

	@Override
	public Date getCreationDate() {
		return new Date();
	}

	@Override
	public Date getLastmodified() {
		return new Date();
	}

	@Override
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(event.toString().getBytes("utf-8"));
	}

	@Override
	public ResourceType getResourceTypes() {
		return resourceType;
	}

	@Override
	public iCalComponent getComponent() {
		return event;
	}
	
}
