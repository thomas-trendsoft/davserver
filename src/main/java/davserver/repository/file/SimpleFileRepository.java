package davserver.repository.file;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileExistsException;

import davserver.DAVServer;
import davserver.protocol.auth.IAuthenticationProvider;
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

/**
 * Basic File WebDAV server implementation
 * 
 * @author tkrieger
 *
 */
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
	 * Defaultconstructor 
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
	public void remove(String uri) throws IOException, NotFoundException, NotAllowedException, LockedException {
		Path res = fs.getPath(root.toString(), uri);
		
		// check exists
		if (!Files.exists(res)) {
			throw new NotFoundException("no such file or directory");
		}
		
		// check write access
		//if (!Files.isWritable(res)) {
		//	throw new NotAllowedException("is not writeable");
		//}
		
		// remove the file or directory 
		try {
			Files.delete(res);
		} catch (DirectoryNotEmptyException e) {
			throw new NotAllowedException("collection is not empty");
		} 

	}

	@Override
	public Collection createCollection(String ref) throws IOException,NotAllowedException, ResourceExistsException, ConflictException {
		Path coll = fs.getPath(root.toString(), ref);
		Path dir  = null;
		
		try {
			dir = Files.createDirectory(coll);
		} catch (FileExistsException e) {
			throw new ResourceExistsException(ref + " exits already");
		} 	
		
		return new FileCollection(dir);
	}

	@Override
	public Resource createResource(String ref, InputStream data)
			throws NotAllowedException, ConflictException, ResourceExistsException, NotFoundException, IOException {
		Path res = fs.getPath(root.toString(), ref);
		
		// check if directory
		if (Files.isDirectory(res)) {
			throw new NotAllowedException("is a directory");
		}
		
		// check write access
		//if (!Files.isWritable(res)) {
		//	throw new NotAllowedException("is not writeable");
		//}
		
		// copy data to file (fail in cause of nio ?)
		// Files.copy(data, res,StandardCopyOption.REPLACE_EXISTING);

		FileOutputStream out = new FileOutputStream(res.toAbsolutePath().toString());
		byte[] puf = new byte[512];
		int    r   = -1;
		
		while ((r = data.read(puf)) > 0) {
			out.write(puf,0,r);
		}

		out.close();
		
		return new FileResource(res);
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

	@Override
	public boolean needsAuth() {
		return false;
	}

	@Override
	public IAuthenticationProvider getAuthProvider() {
		return null;
	}

}
