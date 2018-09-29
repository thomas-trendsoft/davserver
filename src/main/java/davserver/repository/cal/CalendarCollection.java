package davserver.repository.cal;

import java.util.Date;
import java.util.Iterator;

import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

/**
 * calendar collection implementation
 * 
 * @author tkrieger
 *
 */
public class CalendarCollection extends Collection{

	/**
	 * Defaultconstructor 
	 * @param name
	 */
	public CalendarCollection(String name) {
		super(name);
	}

	@Override
	public Resource getChild(String name) {
		return null;
	}

	@Override
	public Iterator<Resource> getChildIterator() {
		return null;
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
		return null;
	}

	@Override
	public Date getLastmodified() {
		return null;
	}

}
