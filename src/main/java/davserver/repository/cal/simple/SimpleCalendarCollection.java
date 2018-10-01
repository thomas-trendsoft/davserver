package davserver.repository.cal.simple;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.cal.CalendarCollection;

/**
 * Simple memory managed calendar implementation  to test
 * 
 * @author tkrieger
 *
 */
public class SimpleCalendarCollection extends CalendarCollection {

	/**
	 * Child collection
	 */
	private HashMap<String, Resource> childs;
	
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
	public SimpleCalendarCollection(String name) {
		super(name);
		
		this.childs = new HashMap<String,Resource>();
		this.created = new Date();
		this.lm      = new Date();
	}

	@Override
	public Resource getChild(String name) {
		return childs.get(name);
	}
	
	public void removeChild(String name) {
		this.childs.remove(name);
	}
	
	public void addChild(String name,Resource val) {
		this.childs.put(name, val);
	}

	@Override
	public Iterator<Resource> getChildIterator() {
		return childs.values().iterator();
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		return null;
	}

	@Override
	public void remProperty(PropertyRef ref) {
	}

	@Override
	public void setProperty(Property p) {
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return null;
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
