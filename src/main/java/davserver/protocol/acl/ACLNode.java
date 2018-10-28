package davserver.protocol.acl;

import davserver.repository.Property;

public class ACLNode {
	
	private Property owner;
	
	private Property group;

	public Property getOwner() {
		return owner;
	}

	public Property getGroup() {
		return group;
	}


}
