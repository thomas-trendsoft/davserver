package davserver.repository.simple;

import java.util.HashMap;

import davserver.protocol.acl.ACLProvider;
import davserver.protocol.acl.properties.ACL;
import davserver.repository.error.ConflictException;
import davserver.repository.error.NotAllowedException;

public class SimpleACLProvider extends ACLProvider {

	private HashMap<String,ACL> aclStore;

	@Override
	public ACL getResourceACL(String href) {
		return aclStore.get(href);
	}

	@Override
	public void updateACL(String href,ACL acl) throws ConflictException, NotAllowedException {
		ACL src = aclStore.get(href);
		if (src == null) {
			aclStore.put(href, acl);
		} else {
			src.getACList().addAll(acl.getACList());
		}
	}
	
}
