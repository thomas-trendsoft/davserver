package davserver.protocol.acl.properties;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.protocol.acl.ACE;
import davserver.protocol.xml.ElementIterator;
import davserver.repository.Property;
import davserver.utils.XMLParser;

/**
 * Access control list
 * 
 * @author tkrieger
 *
 */
public class ACL extends Property {
	
	/**
	 * internal access control
	 */
	private List<ACE> acl;

	/**
	 * Defaultconstructor 
	 */
	public ACL() {
		super(DAVServer.Namespace,"acl",null);
		this.acl = new LinkedList<>();
	}

	public List<ACE> getACList() {
		return acl;
	}
	
	/**
	 * parse an xml acl 
	 * 
	 * @param doc
	 * @return
	 */
	public static ACL parse(Document doc) {
		ACL      acl = new ACL();
		Element root = doc.getDocumentElement();
		
		if (root == null || DAVServer.Namespace.compareTo(root.getNamespaceURI()) != 0 || "acl".compareTo(root.getLocalName()) != 0)
			return null;
		
		ElementIterator eiter = new ElementIterator(root);
		while (eiter.hasNext()) {
			Element c = eiter.next();
			if (DAVServer.Namespace.compareTo(c.getNamespaceURI())==0 && "ace".compareTo(c.getLocalName())==0) {
				ACE ace = ACE.parse(c);
				acl.acl.add(ace);
			}
		}
		
		return acl;
	}

}
