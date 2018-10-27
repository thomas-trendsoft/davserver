package davserver.protocol.xml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import davserver.utils.XMLParser;

/**
 * Base list element for prop multistatus etc.
 * 
 * @author tkrieger
 *
 */
public class ListElement extends DAVXMLObject {
	
	/**
	 * Root namespace
	 */
	private String namespace;
	
	/**
	 * Root name
	 */
	private String name;
	
	/**
	 * Child Elements
	 */
	private List<DAVXMLObject> childs;

	/**
	 * Defaultkonstruktor 
	 * 
	 * @param name
	 * @param ns
	 */
	public ListElement(String name,String ns) {
		this.name      = name;
		this.namespace = ns;
		this.childs    = new LinkedList<>();
	}
	
	/**
	 * Add new child element
	 * 
	 * @param child
	 */
	public void addChild(DAVXMLObject child) {
		childs.add(child);
	}
	
	/**
	 * Create list xml element
	 */
	@Override
	public Element appendXML(Element root) {
		Element lr = root.getOwnerDocument().createElementNS(namespace, name);
		
		for (DAVXMLObject c : childs) {
			c.appendXML(lr);
		}
		
		root.appendChild(lr);
		
		return lr;
	}
	
	public Document createDocument() throws ParserConfigurationException {
		Document doc = XMLParser.singleton().createDocument();
		Element  lr  = doc.createElementNS(namespace, "D:" + name);
		
		for (DAVXMLObject c : childs) {
			c.appendXML(lr);
			System.out.println("add child");
		}
		
		doc.appendChild(lr);
		
		return doc;
	}

}
