package davserver.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Property {
	
	private String namespace;

	private String name;
	
	private Object value;
	
	public Property(String ns,String name,Object val) {
		this.namespace = ns;
		this.name = name;
		this.value = val;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public Element toXML(Document doc) {
		Element elem = doc.createElementNS(namespace, name);
		if (value != null) {
			elem.setTextContent(String.valueOf(value));
		}
		return elem;
	}
	
}
