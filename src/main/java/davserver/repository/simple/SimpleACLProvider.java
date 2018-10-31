package davserver.repository.simple;

import java.util.HashMap;

import davserver.protocol.acl.ACLProvider;
import davserver.protocol.acl.properties.ACL;

public class SimpleACLProvider extends ACLProvider {

	private HashMap<String,ACL> aclStore;

	@Override
	public ACL getResourceACL(String href) {
		return aclStore.get(href);
	}
	
}
