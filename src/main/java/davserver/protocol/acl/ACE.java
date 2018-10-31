package davserver.protocol.acl;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.protocol.xml.DAVXMLObject;
import davserver.protocol.xml.ElementIterator;

/**
 * Acess control element
 * 
 * @author tkrieger
 *
 */
public class ACE extends DAVXMLObject {

	private String principal;
	
	private boolean deny;
	
	private boolean readonly;
	
	private boolean inherited;

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public boolean isDeny() {
		return deny;
	}

	public void setDeny(boolean deny) {
		this.deny = deny;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isInherited() {
		return inherited;
	}

	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}

	@Override
	public Element appendXML(Element root) {
		return null;
	}
	
	public static ACE parse(Element root) {
		int subs = 0;
		ACE  ace = new ACE();
		
		ElementIterator iter = new ElementIterator(root);
		while (iter.hasNext()) {
			Element c = iter.next();
			if (DAVServer.Namespace.compareTo(c.getNamespaceURI())==0) {
				if ("principal".compareTo(c.getLocalName())==0) {
					// check possible content
				} else if ("grant".compareTo(c.getLocalName())==0) {
					subs = 1;
				} else if ("deny".compareTo(c.getLocalName())==0) {
					subs = 2;
				}
				if (subs > 0) {
					ace.deny = (1 != subs);
					ElementIterator siter = new ElementIterator(c);
					while (siter.hasNext()) {
						Element priv = siter.next();
					}
				}
			}
		}
		
		return ace;
	}
	
	
}
