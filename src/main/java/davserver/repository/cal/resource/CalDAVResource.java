package davserver.repository.cal.resource;

import davserver.repository.Resource;
import ical4dav.properties.iCalComponent;

public abstract class CalDAVResource extends Resource {

	public CalDAVResource(String name) {
		super(name);
	}
	
	public abstract iCalComponent getComponent();

}
