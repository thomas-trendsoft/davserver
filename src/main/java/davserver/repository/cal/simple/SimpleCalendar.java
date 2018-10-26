package davserver.repository.cal.simple;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;

import davserver.DAVServer;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.cal.VCalendar;

/**
 * Memory managed calendar implementation 
 * 
 * @author tkrieger
 *
 */
public class SimpleCalendar extends VCalendar {

	/**
	 * Memory childs
	 */
	private HashMap<String,Resource> childs;

	/**
	 * property map
	 */
	private HashMap<String,Property> properties;
	
	/**
	 * creation date
	 */
	private Date created;
	
	/**
	 * last modified
	 */
	private Date lm;	
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param name
	 */
	public SimpleCalendar(String name) {
		super(name);
		
		childs     = new HashMap<String,Resource>();
		properties = new HashMap<String,Property>();
		created    = new Date();
		lm         = new Date();
		
		properties.put(DAVServer.Namespace + "owner", new Property(DAVServer.Namespace, "owner", "admin"));
	}

	@Override
	public Resource getChild(String name) {
		return childs.get(name);
	}
	
	public void addChild(String name,Resource c) {
		this.childs.put(name, c);
	}

	@Override
	public Iterator<Resource> getChildIterator() {
		return childs.values().iterator();
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		return properties.get(ref.getNs() + ref.getName());
	}

	@Override
	public void remProperty(PropertyRef ref) {
		properties.remove(ref.getNs() + ref.getName());
	}

	@Override
	public void setProperty(Property p) {
		properties.put(p.getNamespace() + p.getName(), p);
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
