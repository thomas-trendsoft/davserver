package davserver.repository;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for a dav repository collection 
 * 
 * @author tkrieger
 *
 */
public abstract class Collection extends Resource {

	/***
	 * Query a collection child with name 
	 * 
	 * @param name
	 * @return
	 */
	public abstract Resource getChild(String name);
	
	public int getContentLength() {
		return 0;
	}
	
	public InputStream getContent() throws IOException {
		return null;
	}

	
}
