package davserver.protocol.acl;

import java.util.LinkedList;
import java.util.List;

import davserver.DAVServer;
import davserver.repository.Property;

/**
 * Access control list
 * 
 * @author tkrieger
 *
 */
public class ACL extends Property {
	
	/**
	 * internal access control
	 */
	private List<ACE> acl;

	/**
	 * Defaultconstructor 
	 */
	public ACL() {
		super(DAVServer.Namespace,"acl",null);
		this.acl = new LinkedList<>();
	}

}
