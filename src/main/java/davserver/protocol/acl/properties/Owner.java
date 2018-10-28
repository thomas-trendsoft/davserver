package davserver.protocol.acl.properties;

import davserver.DAVServer;
import davserver.repository.Property;

public class Owner extends Property {

	public Owner() {
		super(DAVServer.Namespace, "owner",null);
	}

}
