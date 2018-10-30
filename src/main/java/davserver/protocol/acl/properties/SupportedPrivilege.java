package davserver.protocol.acl.properties;

import java.util.HashMap;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.protocol.acl.privilege.Privilege;
import davserver.protocol.xml.DAVXMLObject;

public class SupportedPrivilege extends DAVXMLObject{

	private Privilege privilege;
	
	private HashMap<String,String> descriptions;

	public Privilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}

	public HashMap<String,String> getDescription() {
		return descriptions;
	}

	@Override
	public Element appendXML(Element root) {
		Element data = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "supported-privilege");
		
		for (String l : descriptions.keySet()) {
			Element desc = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "description");	
			desc.setAttribute("xml:lang", l);
			desc.setTextContent(descriptions.get(l));
		}
		
		privilege.appendXML(data);
		
		root.appendChild(data);
		
		return data;
	}
	
	
	
}
