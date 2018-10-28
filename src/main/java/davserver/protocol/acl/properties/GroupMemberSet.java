package davserver.protocol.acl.properties;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.repository.Property;

public class GroupMemberSet extends Property {

	private List<String> uris;
	
	public GroupMemberSet() {
		super(DAVServer.Namespace, "group-member-set", null);
		
		uris = new LinkedList<>();
	}

	public List<String> getUris() {
		return uris;
	}
	
	@Override
	public Element appendXML(Element root) {
		Element aus = root.getOwnerDocument().createElementNS(DAVServer.Namespace, this.getName());
		
		for (String h : uris) {
			Element href = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "href");
			href.setTextContent(h);
			aus.appendChild(href);
		}
		
		root.appendChild(aus);
		
		return aus;
	}

}
