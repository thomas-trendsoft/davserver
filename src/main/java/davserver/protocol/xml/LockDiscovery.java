package davserver.protocol.xml;

import java.util.Collection;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.repository.LockEntry;
import davserver.repository.Property;

/**
 * Lock discovery property implementation
 * 
 * @author tkrieger
 *
 */
public class LockDiscovery extends Property {

	/**
	 * active lock list
	 */
	private Collection<LockEntry> locks;
	
	/**
	 * Defaultkonstruktor 
	 */
	public LockDiscovery(Collection<LockEntry> locks) {
		super(DAVServer.Namespace,"lockdiscovery",null);
		
		this.locks = locks;
	}
	
	/**
	 * append xml presentation of lock discovery
	 */
	@Override
	public Element appendXML(Element root) {
		Element dr = super.appendXML(root);
		
		if (locks != null) {
			for (LockEntry l : locks) {
				l.appendXML(dr);
			}
		}
		
		return dr;
	}

}
