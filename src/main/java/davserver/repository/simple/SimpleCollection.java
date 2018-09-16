package davserver.repository.simple;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import davserver.DAVServer;
import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class SimpleCollection extends Collection {
	
	private HashMap<String,Property> properties;
	
	private HashMap<String,Resource> childs;
	
	public SimpleCollection(String name) {
		super(name);
		properties = new HashMap<String,Property>();
		properties.put("getlastmodified", new Property(DAVServer.Namespace,"getlastmodified",new Date(0)));
		properties.put("creationdate",new Property(DAVServer.Namespace,"creationdate",new Date(0)));
		childs = new HashMap<String,Resource>();
	}
	
	public HashMap<String,Resource> getChilds() {
		return childs;
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		return properties.get(ref.getName());
	}
	
	public Resource getChild(String name) {
		return childs.get(name);
	}
	
	public void addChild(String name,Resource r) {
		childs.put(name, r);
	}
	
	public String toString(String pre) {
		String ret = "";
		for (String n : childs.keySet()) {
			Resource r  = childs.get(n);
			boolean col = r instanceof SimpleCollection;
			ret += pre + n + (col ? "(C)" : "(R)") + "\n";
			if (r instanceof SimpleCollection) {
				ret += ((SimpleCollection) r).toString(pre + "  ");
			}
		}
		return ret;		
	}

	@Override
	public String toString() {
		return toString("");
	}

	@Override
	public Iterator<Resource> getChildIterator() {
		return childs.values().iterator();
	}
	
	@Override
	public void setProperty(Property p) {
		if (p != null)
			this.properties.put(p.getName(), p);
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return properties.values().iterator();
	}

}
