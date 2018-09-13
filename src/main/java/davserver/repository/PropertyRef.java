package davserver.repository;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

public class PropertyRef {
	
	public static int ALLPROP = 0;
	
	public static int PROP = 1;
	
	public static int PROPNAMES = 2;
	
	private int type;
	
	private String ns;
	
	private String name;
	
	private List<PropertyRef> subrefs;
	
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
