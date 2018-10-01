package davserver.protocol.xml;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.repository.Property;
import davserver.utils.Pair;

public class ResourceType extends Property {

	/**
	 * supported resource types
	 */
	private List<Pair<String,String>> types;
	
	/**
	 * Defaultkonstruktor 
	 */
	public ResourceType() {
		super(DAVServer.Namespace, "resourcetype", null);
		
		types = new LinkedList<>();
	}
	
	public void addType(String ns,String name) {
		types.add(new Pair<String, String>(ns, name));
	}
	
	@Override
	public Element appendXML(Element doc,boolean content) {
		Element elem = super.appendXML(doc,content);
		Element type = null;

		for (Pair<String,String> t : types) {
			System.out.println("add type: " + t);
			if (t.getKey() == null) {
				if (t.getValue() != null) {
					type = doc.getOwnerDocument().createElement(t.getValue());
				}
			} else {
				type = doc.getOwnerDocument().createElementNS(t.getKey(), t.getValue());
			}
			elem.appendChild(type);
		}
		
		return elem;
	}

}
