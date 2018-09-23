package davserver.repository;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import davserver.DAVServer;

/**
 * LockEntry for a resource 
 * 
 * @author tkrieger
 *
 */
public class LockEntry {
	
	/**
	 * Integer flag to normal write lock 
	 */
	public static final int WRITE_LOCK = 0;
	
	/**
	 * Resource reference
	 */
	private String ref;
	
	/**
	 * Lock owner list
	 */
	private HashSet<String> owner;
	
	/**
	 * Flag if shared lock
	 */
	private boolean shared;
	
	/**
	 * Lock type
	 */
	private int type;
	
	/**
	 * Timeout value (date from seconds)
	 */
	private long timeout;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param ref
	 */
	public LockEntry(String ref) {
		this.ref   = ref;
		this.owner = new HashSet<String>();
		this.type  = WRITE_LOCK;
	}
	
	/**
	 * Create lock entry from lockinfo element
	 * 
	 * @param doc
	 * @return
	 */
	public static LockEntry parse(String href,Document doc) {
		Element root  = doc.getDocumentElement();
		Node    child = root.getFirstChild();
		LockEntry  le = new LockEntry(href);
		
		// check root scope
		if (root.getLocalName().compareTo("lockinfo") != 0 || root.getNamespaceURI().compareTo(DAVServer.Namespace)!=0) {
			return null;
		}
		
		// check child elments
		while (child != null) {
			if (child instanceof Element && DAVServer.Namespace.compareTo(child.getNamespaceURI()) == 0) {
				// lock scope
				if (child.getLocalName().compareTo("lockscope")==0) {
					Node schild = child.getFirstChild();
					while (schild != null) {
						if (schild instanceof Element && DAVServer.Namespace.compareTo(schild.getNamespaceURI())==0) {
							if (schild.getLocalName().compareTo("exclusive")==0) {
								le.setShared(false);
								break;
							} else if (schild.getLocalName().compareTo("shared")==0) {
								le.setShared(true);
								break;
							}
						}
						schild = schild.getNextSibling();
					}
				}
				// lock type
				if (child.getLocalName().compareTo("locktype")==0) {
					Node schild = child.getFirstChild();
					while (schild != null) {
						if (schild instanceof Element && DAVServer.Namespace.compareTo(schild.getNamespaceURI())==0) {
							if (schild.getLocalName().compareTo("write")==0) {
								le.setType(LockEntry.WRITE_LOCK);
							} else {
								System.out.println("unkown lock type: " + schild.getLocalName());
							}
						}						
						schild = schild.getNextSibling();
					}					
				}
				// owner list
				if (child.getLocalName().compareTo("owner")==0) {
					Node schild = child.getFirstChild();
					while (schild != null) {
						if (schild instanceof Element && DAVServer.Namespace.compareTo(schild.getNamespaceURI())==0) {
							if (schild.getLocalName().compareTo("href")==0) {
								le.getOwner().add(schild.getTextContent());
							}
						}
						schild = schild.getNextSibling();
					}					
				}
			}
			child = child.getNextSibling();
		}
		
		return le;
	}

	/**
	 * Get the resource reference
	 * 
	 * @return
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * Get the lock owner list
	 * 
	 * @return
	 */
	public HashSet<String> getOwner() {
		return owner;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
}
