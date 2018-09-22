package davserver.repository.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class FileResource extends Resource {

	public FileResource(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remProperty(PropertyRef ref) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperty(Property p) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getETag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Date getCreationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLastmodified() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getContent() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
