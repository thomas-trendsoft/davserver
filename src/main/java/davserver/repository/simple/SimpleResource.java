package davserver.repository.simple;

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
	
	public String getContent() {
		return content;
	}

}
