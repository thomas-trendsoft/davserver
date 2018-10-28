package davserver.protocol.acl;

import org.w3c.dom.Element;

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
	
	public AlternateURISet getAlternateURIset() {
		return alternateURIset;
	}

	@Override
	public Element appendXML(Element root) {
		return null;
	}

}
