package davserver.repository.cal.properties;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.repository.Property;

/**
 * Implementation of supported calendar component set property 
 * 
 * @author tkrieger
 *
 */
public class SupportedCalendarCompSet extends Property {

	/**
	 * list of supported component types
	 */
	private List<String> components;
	
	/**
	 * Defaultconstructor 
	 */
	public SupportedCalendarCompSet(List<String> comps) {
		super(DAVServer.CalDAVNS,"supported-calendar-component-set",null);
		
		components = new LinkedList<>();
		
		if (comps != null)
			components.addAll(comps);
	}
	
	public List<String> getComponents() {
		return components;
	}

	@Override
	public Element appendXML(Element root) {
		Element elem = root.getOwnerDocument().createElementNS(this.getNamespace(), this.getName());
		
		for (String c : components) {
			Element s = root.getOwnerDocument().createElementNS(this.getNamespace(), "comp");
			s.setAttribute("name", c);
			elem.appendChild(s);
		}
		
		root.appendChild(elem);
		return elem;
		
	}

	
	
}
