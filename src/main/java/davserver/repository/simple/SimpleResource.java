package davserver.repository.simple;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;

import davserver.DAVServer;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class SimpleResource extends Resource {
	
	private HashMap<String,Property> properties;
	
	private String content;
	
	public SimpleResource() {
		properties = new HashMap<String,Property>();
		properties.put("getlastmodified", new Property(DAVServer.Namespace,"getlastmodified",new Date(0)));
		properties.put("creationdate",new Property(DAVServer.Namespace,"creationdate",new Date(0)));
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		System.out.println("get property: " + ref.getName());
		return properties.get(ref.getName());
	}
	
	public void setContent(String str) {
		this.content = str;
	}
	
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(content.getBytes("utf-8"));
	}

	@Override
	public int getContentLength() {
		try {
			return content.getBytes("utf-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return content.length();
		}
	}


}
