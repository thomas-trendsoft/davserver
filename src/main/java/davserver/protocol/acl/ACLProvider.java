package davserver.protocol.acl;

import davserver.protocol.acl.properties.ACL;

public abstract class ACLProvider {
	
	public abstract ACL getResourceACL(String href);
		

}
