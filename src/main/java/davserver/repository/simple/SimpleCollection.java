package davserver.repository.simple;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class SimpleCollection extends Collection {
	
	/**
	 * property map
	 */
	private HashMap<String,Property> properties;
	
	/**
	 * child map
	 */
	private HashMap<String,Resource> childs;
	
	/**
	 * last modified
	 */
	private Date lm;
	
	/**
	 * creation date
	 */
	private Date created;
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param name
	 */
	public SimpleCollection(String name) {
		super(name);
		lm = new Date();
		created = new Date();
		properties = new HashMap<String,Property>();
		childs = new HashMap<String,Resource>();
	}
	
	@Override
	public void remProperty(PropertyRef ref) {
		properties.remove(ref.getNs() + ref.getName());
	}
	
	public HashMap<String,Resource> getChilds() {
		return childs;
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		return properties.get(ref.getNs() + ref.getName());
	}
	
	public Resource getChild(String name) {
		return childs.get(name);
	}
	
	public void addChild(String name,Resource r) {
		childs.put(name, r);
		lm = new Date();
	}
	
	/**
	 * string representation for debug purpose
	 * 
	 * @param pre
	 * @return
	 */
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
			this.properties.put(p.getNamespace() + p.getName(), p);
		lm = new Date();
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return properties.values().iterator();
	}

	@Override
	public Date getCreationDate() {
		return created;
	}

	@Override
	public Date getLastmodified() {
		return lm;
	}

}
