package davserver.repository;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Property reference class 
 * 
 * @author tkrieger
 *
 */
public class PropertyRef {
	
	/**
	 * Flag for the allprop reference
	 */
	public static int ALLPROP = 0;
	
	/**
	 * Flag for the prop reference
	 */
	public static int PROP = 1;
	
	/**
	 * Flag for the propname reference
	 */
	public static int PROPNAMES = 2;
	
	/**
	 * Reference type identifier
	 */
	private int type;
	
	/**
	 * Property namespace
	 */
	private String ns;
	
	/**
	 * Property name
	 */
	private String name;
	
	/**
	 * Sub list of references (prop Tag)
	 */
	private List<PropertyRef> subrefs;
	
	/**
	 * Simple name constructor 
	 * 
	 * @param name Property name
	 */
	public PropertyRef(String name) {
		this.ns = null;
		this.name = name;
	}
	
	public PropertyRef(Element elem) {
		this.ns   = elem.getNamespaceURI();
		this.name = elem.getLocalName();
	}
	
	public PropertyRef(int type) {
		this.type = type;
		this.subrefs = new LinkedList<PropertyRef>();
	}
	
	public List<PropertyRef> getSubRefs() {
		return subrefs;
	}
	
	public int getType() {
		return type;
	}

	public String getNs() {
		return ns;
	}

	public String getName() {
		return name;
	}
	
	
}
