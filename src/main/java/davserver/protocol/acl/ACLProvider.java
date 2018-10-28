package davserver.protocol.acl;


public abstract class ACLProvider {
	
	public abstract ACL getResourceACL(String href);
		

}
