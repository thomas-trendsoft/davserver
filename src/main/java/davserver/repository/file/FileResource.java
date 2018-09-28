package davserver.repository.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Iterator;

import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class FileResource extends Resource {

	/**
	 * Reference to local file
	 */
	private Path file;
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param file
	 */
	public FileResource(Path file) {
		super(file.getFileName().toString());
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
		try {
			return Files.size(file);
		} catch (IOException e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public Date getCreationDate() {
		try {
			BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
			return new Date(attr.creationTime().toMillis());
		} catch (IOException e) {
			e.printStackTrace();
			return new Date(0);
		}
	}

	@Override
	public Date getLastmodified() {
		try {
			return new Date(Files.getLastModifiedTime(file).toMillis());
		} catch (IOException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	@Override
	public InputStream getContent() throws IOException {
		return new FileInputStream(file.toAbsolutePath().toString());
	}

}
