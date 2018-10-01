package davserver.repository.cal.simple;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.cal.Calendar;

/**
 * Memory managed calendar implementation 
 * 
 * @author tkrieger
 *
 */
public class SimpleCalendar extends Calendar {

	/**
	 * Memory childs
	 */
	private HashMap<String,Resource> childs;

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
		
		childs  = new HashMap<String,Resource>();
		created = new Date();
		lm      = new Date();
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
