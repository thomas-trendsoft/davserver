package davserver.repository;

import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import davserver.DAVServer;
import davserver.DAVUtil;
import davserver.protocol.xml.DAVXMLObject;

/**
 * Resource property 
 * 
 * @author tkrieger
 *
 */
public class Property extends DAVXMLObject {
	
	/**
	 * Default DAV Properties
	 */
	private static HashMap<String,PropertyRef> davProperties = null;
		
	/**
	 * Property namespace 
	 */
	private String namespace;

	/**
	 * Property name
	 */
	private String name;
	
	/**
	 * Property value
	 */
	private Object value;
	
	/**
	 * Live flag
	 */
	private boolean live;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param ns Namespace
	 * @param name Name
	 * @param val Value object 
	 */
	public Property(String ns,String name,Object val) {
		this.namespace = ns;
		this.name = name;
		this.value = val;
	}
	
	/**
	 * XML Constructor 
	 * 
	 * @param root
	 */
	public Property(Element root) {
		this.name = root.getLocalName();
		this.namespace = root.getNamespaceURI();
		this.value = root.getFirstChild();
	}

	/**
	 * Get the property name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the property name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the value object
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Set a new value object
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Get the property namespace
	 * @return
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Set the property namespace
	 * @return
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	/**
	 * Create the xml presentation of this property 
	 * 
	 * @param doc
	 * @return
	 */
	public Element appendXML(Element doc,boolean content) {
		Element elem = doc.getOwnerDocument().createElementNS(namespace, name);
		if (value != null && content) {
			if (value instanceof Node) {
				Node cn = doc.getOwnerDocument().adoptNode((Node)value);
				elem.appendChild(cn);
			} else {
				elem.setTextContent(String.valueOf(value));				
			}
		}
		doc.appendChild(elem);
		return elem;
	}
	
	/**
	 * DAV Property interface
	 */
	@Override
	public Element appendXML(Element root) {
		return appendXML(root,true);
	}


	/**
	 * live flag 
	 * 
	 * @return
	 */
	public boolean isLive() {
		return live;
	}
	
	/**
	 * Create or get default dav properties 
	 * 
	 * @return
	 */
	public static HashMap<String,PropertyRef> getDAVProperties() {
		if (davProperties == null) {
			davProperties = new HashMap<String,PropertyRef>();
			davProperties.put(DAVServer.Namespace + "getcontentlength",new PropertyRef(PropertyRef.DAV_CONTENTLENGTH));
			davProperties.put(DAVServer.Namespace + "getetag",new PropertyRef(PropertyRef.DAV_ETAG));
			davProperties.put(DAVServer.Namespace + "getcontenttype",new PropertyRef(PropertyRef.DAV_CONTENTTYPE));
			davProperties.put(DAVServer.Namespace + "creationdate",new PropertyRef(PropertyRef.DAV_CREATIONDATE));
			davProperties.put(DAVServer.Namespace + "getlastmodified",new PropertyRef(PropertyRef.DAV_LASTMODIFIED));
			davProperties.put(DAVServer.Namespace + "displayname",new PropertyRef(PropertyRef.DAV_DISPLAYNAME));
			davProperties.put(DAVServer.Namespace + "lockdiscovery",new PropertyRef(PropertyRef.DAV_LOCKDISCOVERY));
			davProperties.put(DAVServer.Namespace + "supportedlock",new PropertyRef(PropertyRef.DAV_SUPPORTEDLOCK));
		}
		return davProperties;
	}
	
	/**
	 * Help method to create the default DAV Properties 
	 * 
	 * @param ref
	 * @param r
	 * @return
	 */
	public static Property getDAVProperty(String ref,Resource r,IRepository repos) {
		Property    p  = null;
		Date        d  = null;
		PropertyRef dp = Property.getDAVProperties().get(ref);
		
		System.out.println(dp + " " + ref);
		if (dp == null) 
			return null;
		
		switch (dp.getType()) {
		case PropertyRef.DAV_CONTENTLENGTH:
			System.out.println("create cl: " + dp.getName());
			p = new Property(DAVServer.Namespace, "getcontentlength", r.getContentLength());
			break;
		case PropertyRef.DAV_ETAG:
			p = new Property(DAVServer.Namespace, "getetag", r.getETag());
			break;
		case PropertyRef.DAV_DISPLAYNAME:
			p = new Property(DAVServer.Namespace, "displayname", r.getName());
			break;
		case PropertyRef.DAV_LASTMODIFIED:
			d = r.getLastmodified();
			if (d == null)
				d = new Date(0);
			p = new Property(DAVServer.Namespace, "getlastmodified", DAVUtil.dateFormat.format(d));
			break;								
		case PropertyRef.DAV_CREATIONDATE:
			d = r.getCreationDate();
			if (d == null)
				d = new Date(0);
			p = new Property(DAVServer.Namespace, "creationdate", DAVUtil.dateFormat.format(d));
			break;	
		case PropertyRef.DAV_CONTENTTYPE:
			p = new Property(DAVServer.Namespace, "getcontenttype", r.getContentType());
			break;
		case PropertyRef.DAV_LOCKDISCOVERY:
			ILockManager lm = repos.getLockManager();
			if (!repos.supportLocks() || (p = lm.checkLocked(ref)) == null)  {
				p = new Property(DAVServer.Namespace,"lockdiscovery",null);
			} 
			break;
		case PropertyRef.DAV_SUPPORTEDLOCK:
			if (!repos.supportLocks()) {
				p = repos.getLockManager().getSupportedLocks();				
			} else {
				p = new Property(DAVServer.Namespace,"supportedlock",null);
			}
			break;
		}
		
		return p;
	}


}
