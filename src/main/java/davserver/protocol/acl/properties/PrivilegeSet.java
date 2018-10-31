package davserver.protocol.acl.properties;

import davserver.DAVServer;
import davserver.repository.Property;

/**
 * Supported privileges for a resource 
 * 
 * @author tkrieger
 *
 */
public class PrivilegeSet extends Property {

	public PrivilegeSet(String name) {
		super(DAVServer.Namespace, name, null);
	}

}
