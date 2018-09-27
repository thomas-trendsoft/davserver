package davserver.repository.card;

import java.util.Date;
import java.util.Iterator;

import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class AddressBook extends Collection {

	public AddressBook(String name) {
		super(name);
	}

	@Override
	public Resource getChild(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Resource> getChildIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remProperty(PropertyRef ref) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(Property p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getCreationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLastmodified() {
		// TODO Auto-generated method stub
		return null;
	}

}
