package davserver.protocol.acl;

import davserver.DAVServer;
import davserver.repository.Property;

public class Group extends Property {
	
	public Group() {
		super(DAVServer.Namespace,"group",null);
	}

}
