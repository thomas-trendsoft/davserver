package davserver.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import davserver.DAVServer;

public class ResourceType extends Property {

	private String typeNS;

	private String typeName;
	
	public ResourceType(String namespace,String name) {
		super(DAVServer.Namespace, "resourcetype", null);
		
		this.typeNS   = namespace;
		this.typeName = name;
	}
	
	@Override
	public Element toXML(Document doc) {
		Element elem = super.toXML(doc);
		Element type = null;
		
		if (typeNS == null) {
			if (typeName != null) {
				type = doc.createElement(typeName);
			}
		} else {
			type = doc.createElementNS(typeNS, typeName);
		}
		
		if (type != null) {
			elem.appendChild(type);
		}
		
		return elem;
	}

}
