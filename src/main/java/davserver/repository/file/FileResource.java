package davserver.repository.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class FileResource extends Resource {

	/**
	 * Reference to local file
	 */
	private File file;
	
	public FileResource(File file) {
		super(file.getName());
		this.file = file;
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remProperty(PropertyRef ref) {
		// can't really remove properties i think
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
	public long getContentLength() {
		return file.length();
	}

	@Override
	public Date getCreationDate() {
		// check java nio
		return new Date(0);
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
