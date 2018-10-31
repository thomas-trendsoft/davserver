package davserver.protocol.acl;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.protocol.acl.properties.AlternateURISet;
import davserver.protocol.xml.DAVXMLObject;
import davserver.repository.Property;

/**
 * ACL Principal Implementation
 * 
 * @author tkrieger
 *
 */
public class Principal extends DAVXMLObject {
	
	private AlternateURISet alternateURIset;
	
	private Property principalURL;
	
	public Principal(String href) {
		this.principalURL    = new Property(DAVServer.Namespace, "principal-URL", href);
		this.alternateURIset = new AlternateURISet();
	}
	
	public AlternateURISet getAlternateURIset() {
		return alternateURIset;
	}
	
	public Property getPrincipalURL() {
		return principalURL;
	}

	@Override
	public Element appendXML(Element root) {
		return null;
	}

}
