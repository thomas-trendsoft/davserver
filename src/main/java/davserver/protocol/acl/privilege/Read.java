package davserver.protocol.acl.privilege;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.protocol.xml.DAVXMLObject;

public class Read extends DAVXMLObject {

	@Override
	public Element appendXML(Element root) {
		Element ret = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "read");
		root.appendChild(ret);
		return ret;
	}

}
