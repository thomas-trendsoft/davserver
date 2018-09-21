package davserver.repository;

import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.DAVUtil;

/**
 * Resource property 
 * 
 * @author tkrieger
 *
 */
public class Property {
	
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
	public Element toXML(Document doc) {
		Element elem = doc.createElementNS(namespace, name);
		if (value != null) {
			elem.setTextContent(String.valueOf(value));
		}
		return elem;
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
			davProperties.put(DAVServer.Namespace + "getcreationdate",new PropertyRef(PropertyRef.DAV_CREATIONDATE));
			davProperties.put(DAVServer.Namespace + "getdisplayname",new PropertyRef(PropertyRef.DAV_DISPLAYNAME));
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
	public static Property getDAVProperty(PropertyRef ref,Resource r) {
		Property    p  = null;
		Date        d  = null;
		PropertyRef dp = Property.getDAVProperties().get(ref.getNs() + ref.getName());
		
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
			p = new Property(DAVServer.Namespace, "getdisplayname", r.getName());
			break;
		case PropertyRef.DAV_LASTMODIFIER:
			d = r.getLastmodified();
			if (d == null)
				d = new Date(0);
			p = new Property(DAVServer.Namespace, "getlastmodified", DAVUtil.dateFormat.format(d));
			break;								
		case PropertyRef.DAV_CREATIONDATE:
			d = r.getCreationDate();
			if (d == null)
				d = new Date(0);
			p = new Property(DAVServer.Namespace, "getcreationdate", DAVUtil.dateFormat.format(d));
			break;								
		}
		
		System.out.println("dav prop: " + dp + " -- " + dp.getType() + " - " + p.getName() + ":" + p.getNamespace());
		return p;
	}

}
