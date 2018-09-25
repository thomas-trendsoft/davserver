package davserver.repository;

import java.util.Date;
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
public class LockEntry extends Property {
	
	/**
	 * Integer flag to normal write lock 
	 */
	public static final String WRITE_LOCK = "write";
	
	/**
	 * Resource reference
	 */
	private String ref;
	
	/**
	 * Lock token
	 */
	protected String token;
	
	/**
	 * Lock owner list
	 */
	private HashSet<String> owner;
	
	/**
	 * Flag if shared lock
	 */
	private boolean shared;
	
	/**
	 * depth value
	 */
	private int depth;
	
	/**
	 * Lock type
	 */
	private String type;
	
	/**
	 * Timeout value (date from seconds)
	 */
	private long timeout;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param ref
	 */
	public LockEntry(String ref,int d,String token) {
		super(DAVServer.Namespace,"lockdiscovery",null);
		
		this.ref   = ref;
		this.owner = new HashSet<String>();
		this.type  = WRITE_LOCK;
		this.token = token;
		
		this.timeout = updatedTimeout();
	}
	
	public static long updatedTimeout() {
		return (new Date().getTime() + 40000);
	}
	
	/**
	 * Create lock entry from lockinfo element
	 * 
	 * @param doc
	 * @return
	 */
	public static LockEntry parse(String href,Document doc,int d,String token) {
		Element root  = doc.getDocumentElement();
		Node    child = root.getFirstChild();
		LockEntry  le = new LockEntry(href,d,token);
		
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
					le.getOwner().add(child.getTextContent());
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getToken() {
		return token;
	}

	public int getDepth() {
		return depth;
	}

	@Override
	public Element appendXML(Element root) {
		Element lr = super.appendXML(root);
		Element al = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "activelock");
		Element ls = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "lockscope");
		
		// lock type
		Element lt  = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "locktype");
		Element lte = root.getOwnerDocument().createElementNS(DAVServer.Namespace, this.getType());
		lt.appendChild(lte);
		
		// lock scope
		if (this.shared) {
			Element sl = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "shared");
			ls.appendChild(sl);
		} else {
			Element sl = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "exclusive");
			ls.appendChild(sl);
		}
		
		// lock depth
		Element dv = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "depth");
		if (depth == 0) {
			dv.setTextContent(String.valueOf(depth));
		} else {
			dv.setTextContent("infinity");
		}
		
		// lock timeout
		Date   now = new Date();
		Element to = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "timeout"); 
		long    tv = timeout - now.getTime();
		
		to.setTextContent("Second-" + String.valueOf(tv));
		
		// lock token
		Element id  = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "locktoken");
		Element idr = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "href");
		id.appendChild(idr);
		idr.setTextContent(token);
		
		// resource ref
		Element lroot = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "lockroot");
		lroot.setTextContent(ref);
		
		Element ol = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "owner");
		for (String o : owner) {
			Element or = root.getOwnerDocument().createElementNS(DAVServer.Namespace,"href");
			or.setTextContent(o);
			ol.appendChild(or);
		}

		al.appendChild(id);
		al.appendChild(dv);
		al.appendChild(to);
		al.appendChild(ol);
		al.appendChild(ls);
		al.appendChild(lroot);
		al.appendChild(lt);
		lr.appendChild(al);
		
		root.appendChild(lr);
		
		return lr;
	}
	
}
