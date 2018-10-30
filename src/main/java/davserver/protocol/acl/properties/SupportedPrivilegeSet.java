package davserver.protocol.acl.properties;

import davserver.DAVServer;
import davserver.repository.Property;

/**
 * Supported privileges for a resource 
 * 
 * @author tkrieger
 *
 */
public class SupportedPrivilegeSet extends Property {

	public SupportedPrivilegeSet() {
		super(DAVServer.Namespace, "supported-privilege-set", null);
	}

}
