package davserver.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Resource property 
 * 
 * @author tkrieger
 *
 */
public class Property {
	
	/**
	 * Property namespace 
	 */
	private String namespace;

	/**
	 * Property name
	 */
	private String name;
	
	/**
	 * Property value
	 */
	private Object value;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param ns Namespace
	 * @param name Name
	 * @param val Value object 
	 */
	public Property(String ns,String name,Object val) {
		this.namespace = ns;
		this.name = name;
		this.value = val;
	}

	/**
	 * Get the property name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the property name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the value object
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Set a new value object
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Get the property namespace
	 * @return
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Set the property namespace
	 * @return
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	/**
	 * Create the xml presentation of this property 
	 * 
	 * @param doc
	 * @return
	 */
	public Element toXML(Document doc) {
		Element elem = doc.createElementNS(namespace, name);
		if (value != null) {
			elem.setTextContent(String.valueOf(value));
		}
		return elem;
	}
	
}
