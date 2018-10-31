package davserver.protocol.acl;

import davserver.protocol.acl.properties.ACL;
import davserver.repository.Property;

public class ACLNode {
	
	private ACL acl;
	
	private Property owner;
	
	private Property group;

	public Property getOwner() {
		return owner;
	}

	public Property getGroup() {
		return group;
	}

	public ACL getACL() {
		return acl;
	}

}
