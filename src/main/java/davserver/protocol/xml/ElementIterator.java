package davserver.protocol.xml;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ElementIterator implements Iterator<Element> {

	public Node currNode;
	
	public ElementIterator(Element root) {
		currNode = root.getFirstChild();
	}
	
	@Override
	public boolean hasNext() {
		while (currNode != null && !(currNode instanceof Element)) {
			currNode = currNode.getNextSibling();			
		}
		return currNode != null;
	}

	@Override
	public Element next() {
		Element ret = null;
		if (currNode != null && currNode instanceof Element)
			ret = (Element) currNode;
		
		if (currNode != null)
			currNode = currNode.getNextSibling();
		
		return ret;
	}

}
