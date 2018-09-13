package davserver.repository.simple;

import java.util.Date;
import java.util.HashMap;

import davserver.DAVServer;
import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class SimpleCollection extends Collection {
	
	private HashMap<String,Property> properties;
	
	private HashMap<String,Resource> childs;
	
	public SimpleCollection() {
		properties = new HashMap<String,Property>();
		properties.put("getlastmodified", new Property(DAVServer.Namespace,"getlastmodified",new Date(0)));
		properties.put("creationdate",new Property(DAVServer.Namespace,"creationdate",new Date(0)));
		childs = new HashMap<String,Resource>();
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		System.out.println("get property: " + ref.getName());
		return properties.get(ref.getName());
	}
	
	public Resource getChild(String name) {
		return childs.get(name);
	}
	
	public void addChild(String name,Resource r) {
		childs.put(name, r);
	}

}
