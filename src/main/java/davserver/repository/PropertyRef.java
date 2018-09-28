package davserver.repository;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Property reference class 
 * 
 * @author tkrieger
 *
 */
public class PropertyRef {
	
	/**
	 * Flag for the allprop reference
	 */
	public static final int ALLPROP = 0;
	
	/**
	 * Flag for the prop reference
	 */
	public static final int PROP = 1;
	
	/**
	 * Flag for the propname reference
	 */
	public static final int PROPNAMES = 2;
	
	/**
	 * Flag to live prop set ref
	 */
	public static final int SUPPORTEDLIVEPROPSET = 3;
	
	/**
	 * Flag to supported report set
	 */
	public static final int SUPPORTEDREPORTSET = 4;
	
	/**
	 * Constant for Content length
	 */
	public static final int DAV_CONTENTLENGTH = 100;
	
	/**
	 * Constant for eTag
	 */
	public static final int DAV_ETAG = 101;
	
	/**
	 * Constant for Content type
	 */
	public static final int DAV_CONTENTTYPE = 102;
	
	/**
	 * Constant for creation date
	 */
	public static final int DAV_CREATIONDATE = 103;
	
	/**
	 * Constant for display name
	 */
	public static final int DAV_DISPLAYNAME = 104;
	
	/**
	 * Constant for last modifier
	 */	
	public static final int DAV_LASTMODIFIED = 105;
	
	/**
	 * Constant for lock discovery
	 */
	public static final int DAV_LOCKDISCOVERY = 106;
	
	/**
	 * Constant for supported locks
	 */
	public static final int DAV_SUPPORTEDLOCK = 107;
	
	/**
	 * Constant for resource type property
	 */
	public static final int DAV_RESOURCETYPE = 108;

	/**
	 * Reference type identifier
	 */
	private int type;
	
	/**
	 * Property namespace
	 */
	private String ns;
	
	/**
	 * Property name
	 */
	private String name;
	
	/**
	 * Sub list of references (prop Tag)
	 */
	private List<PropertyRef> subrefs;
	
	/**
	 * Simple name constructor 
	 * 
	 * @param name Property name
	 */
	public PropertyRef(String name) {
		this.ns = null;
		this.name = name;
	}
	
	public PropertyRef(Element elem) {
		this.ns   = elem.getNamespaceURI();
		this.name = elem.getLocalName();
	}
	
	public PropertyRef(int type) {
		this.type = type;
		this.subrefs = new LinkedList<PropertyRef>();
	}
	
	public List<PropertyRef> getSubRefs() {
		return subrefs;
	}
	
	public int getType() {
		return type;
	}

	public String getNs() {
		return ns;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		String ret = ns + ":" + name + " (" + type + ")";
		return ret;
	}
	
	
}
