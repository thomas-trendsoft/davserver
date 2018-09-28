package davserver.repository.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import davserver.DAVServer;
import davserver.repository.Collection;
import davserver.repository.ILockManager;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.ResourceExistsException;
import davserver.utils.SimpleLockManager;

public class SimpleFileRepository implements IRepository {

	/**
	 * Root Directory
	 */
	private Path root;
	
	/**
	 * Filesystem
	 */
	private FileSystem fs;
	
	/**
	 * Lock Manager
	 */
	private SimpleLockManager lockmanager;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param path
	 */
	public SimpleFileRepository(String path) throws FileNotFoundException {
		
		// init defaults
		fs    = FileSystems.getDefault();
		root  = fs.getPath(path);
		
		if (!Files.exists(root) || !Files.isDirectory(root)) {
			throw new FileNotFoundException();
		} 
		lockmanager = new SimpleLockManager();
	}
	
	@Override
	public Resource locate(String uri) throws NotFoundException, NotAllowedException {
		// open path
		Path child = fs.getPath(root.toString(), uri);
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
	public void remove(String uri) throws NotFoundException, NotAllowedException, LockedException {
	}

	@Override
	public Collection createCollection(String ref)
			throws NotAllowedException, ResourceExistsException, ConflictException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource createResource(String ref, InputStream data)
			throws NotAllowedException, ConflictException, ResourceExistsException, NotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportLocks() {
		return true;
	}

	@Override
	public ILockManager getLockManager() {
		return lockmanager;
	}

	@Override
	public int getProtocol() {
		return DAVServer.PROT_WEBDAV;
	}

}
