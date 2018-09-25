package davserver.protocol.xml;

import org.w3c.dom.Element;

public abstract class DAVXMLObject {
	
	/**
	 * Append the xml object on a given root 
	 * 
	 * @param root
	 */
	public abstract Element appendXML(Element root);

}
