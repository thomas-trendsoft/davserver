package davserver.repository.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

/**
 * Represent a directory as file collection 
 * 
 * @author tkrieger
 *
 */
public class FileCollection extends Collection {

	/**
	 * Local path
	 */
	private Path path;
	
	/**
	 * Filesystem
	 */
	private FileSystem fs;
	
	/**
	 * property map
	 */
	private HashMap<String,Property> properties;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param path
	 */
	public FileCollection(Path path) {
		super(path.getFileName().toString());
		
		this.properties = new HashMap<String,Property>();
		this.path       = path;
		this.fs         = FileSystems.getDefault();
	}

	@Override
	public Resource getChild(String name) {
		Path child = fs.getPath(path.toString(), name);
		// check exists
		if (child != null && Files.exists(child)) {
			// check collection or resource
			if (Files.isDirectory(child)) {
				return new FileCollection(child);
			} else {
				return new FileResource(child);
			}
		} 
		return null;
	}

	@Override
	public Iterator<Resource> getChildIterator() {
        List<Resource> childs = new LinkedList<>();
        
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path p : directoryStream) {
            	if (Files.isDirectory(p))
            		childs.add(new FileCollection(p));
            	else
            		childs.add(new FileResource(p));
            }
        } catch (IOException ex) {
        	System.err.println("error list folder: " + ex.getMessage() + ":" + path.toAbsolutePath());
        }
        
        return childs.iterator();
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
	public Date getCreationDate() {
		try {
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			return new Date(attr.creationTime().toMillis());
		} catch (IOException e) {
			e.printStackTrace();
			return new Date(0);
		}
	}

	@Override
	public Date getLastmodified() {
		try {
			return new Date(Files.getLastModifiedTime(path).toMillis());
		} catch (IOException e) {
			e.printStackTrace();
			return new Date();
		}
	}

}
