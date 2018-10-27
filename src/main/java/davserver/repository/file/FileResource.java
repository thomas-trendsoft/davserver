package davserver.repository.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.properties.ResourceType;

public class FileResource extends Resource {

	/**
	 * Reference to local file
	 */
	private Path file;
	
	/**
	 * property map
	 */
	private HashMap<String,Property> properties;
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param file
	 */
	public FileResource(Path file) {
		super(file.getFileName().toString());
		
		this.file       = file;
		this.properties = new HashMap<String,Property>();
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
		return properties.values().iterator();
	}

	@Override
	public String getETag() {
		try {
			return String.valueOf(Files.getLastModifiedTime(file));
		//	return "d41d8cd98f00b204e9800998ecf8427e";
		} catch (IOException e) {
			e.printStackTrace();
			return String.valueOf((new Date()).getTime());
		}
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

	@Override
	public ResourceType getResourceTypes() {
		return new ResourceType();
	}

}
