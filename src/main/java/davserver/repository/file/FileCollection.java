package davserver.repository.file;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class FileCollection extends Collection {

	/**
	 * Local path
	 */
	private Path path;
	
	/**
	 * Filesystem
	 */
	private FileSystem fs;
	
	public FileCollection(Path path) {
		super(path.getFileName().toString());
		
		this.path = path;
		this.fs   = FileSystems.getDefault();
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
		return null;
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		// no special properties given here
		return null;
	}

	@Override
	public void remProperty(PropertyRef ref) {
		// nothing to do
	}

	@Override
	public void setProperty(Property p) {
		// nothing to do 
		// TODO check exception handling
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return null;
	}

	@Override
	public Date getCreationDate() {
		return new Date();
	}

	@Override
	public Date getLastmodified() {
		return new Date();
	}

}
