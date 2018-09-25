package davserver.protocol.xml;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.repository.Property;

/**
 * Basic Supported Lock Property implementation
 * 
 * @author tkrieger
 *
 */
public class SupportedLocks extends Property {
	
	/**
	 * Defaultkonstruktor 
	 * 
	 */
	public SupportedLocks() {
		super(DAVServer.Namespace,"supportedlock",null);
	}
	
	@Override
	public Element appendXML(Element root) {
		Element r   = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "supportedlock");
		Element les = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "lockentry");
		Element lee = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "lockentry");
		Element lsw = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "write");
		Element lew = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "write");
		Element lle = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "exclusive");
		Element lls = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "shared");
		
		r.appendChild(les);
		r.appendChild(lee);
		
		les.appendChild(lsw);
		lee.appendChild(lew);
		
		les.appendChild(lls);
		lee.appendChild(lle);
		
		root.appendChild(r);
		return r;
	}

}
