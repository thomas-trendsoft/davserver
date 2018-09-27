package davserver.protocol.acl;

import davserver.DAVServer;
import davserver.repository.Property;

public class Owner extends Property {

	public Owner() {
		super(DAVServer.Namespace, "owner",null);
	}

}
