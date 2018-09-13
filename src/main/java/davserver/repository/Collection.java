package davserver.repository;

import java.io.IOException;
import java.io.InputStream;

public abstract class Collection extends Resource {

	public abstract Resource getChild(String name);
	
	public int getContentLength() {
		return 0;
	}
	
	public InputStream getContent() throws IOException {
		return null;
	}

	
}
