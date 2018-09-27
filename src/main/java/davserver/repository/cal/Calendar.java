package davserver.repository.cal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class Calendar extends Resource {

	public Calendar(String name) {
		super(name);
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		return null;
	}

	@Override
	public void remProperty(PropertyRef ref) {
	}

	@Override
	public void setProperty(Property p) {
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return null;
	}

	@Override
	public String getETag() {
		return null;
	}

	@Override
	public long getContentLength() {
		return 0;
	}

	@Override
	public Date getCreationDate() {
		return null;
	}

	@Override
	public Date getLastmodified() {
		return null;
	}

	@Override
	public InputStream getContent() throws IOException {
		return null;
	}

}
