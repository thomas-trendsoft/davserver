package davserver.protocol.acl;

import org.w3c.dom.Element;

import davserver.protocol.xml.DAVXMLObject;

/**
 * Acess control element
 * 
 * @author tkrieger
 *
 */
public class ACE extends DAVXMLObject {

	private Principal principal;
	
	private boolean deny;
	
	private boolean readonly;
	
	private boolean inherited;

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
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
	
	
	
}
