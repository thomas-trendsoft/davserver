package davserver.protocol.acl;

import davserver.protocol.acl.properties.ACL;
import davserver.repository.error.ConflictException;
import davserver.repository.error.NotAllowedException;

public abstract class ACLProvider {
	
	public abstract ACL getResourceACL(String href);
	
	public abstract void updateACL(String href,ACL acl) throws ConflictException, NotAllowedException;
		

}
