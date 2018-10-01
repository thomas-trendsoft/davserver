package davserver.repository.simple;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import davserver.DAVUtil;
import davserver.protocol.xml.ResourceType;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

/**
 * Really simple resource implementation 
 * 
 * @author tkrieger
 *
 */
public class SimpleResource extends Resource {
	
	/**
	 * In memory properties
	 */
	private HashMap<String,Property> properties;
	
	/**
	 * content string
	 */
	private String content;

	/**
	 * last modified
	 */
	private Date lm;
	
	/**
	 * creation timestamp
	 */
	private Date created;
	
	/**
	 * resource type 
	 */
	private ResourceType rType;
	
	/**
	 * defaultconstructor 
	 * 
	 * @param name
	 */
	public SimpleResource(String name) {
		super(name);

		lm = new Date();
		created = new Date();
		rType   = new ResourceType();

		properties = new HashMap<String,Property>();
	}
	
	@Override
	public Property getProperty(PropertyRef ref) {
		System.out.println("get property: " + ref.getName());
		return properties.get(ref.getNs() + ref.getName());
	}
	
	public void setContent(String str) {
		this.content = str;
		this.lm      = new Date();
	}
	
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(content.getBytes("utf-8"));
	}

	@Override
	public long getContentLength() {
		try {
			return content.getBytes("utf-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return content.length();
		}
	}


	@Override
	public String getETag() {
		try {
			return DAVUtil.createHash(this.content);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return UUID.randomUUID().toString();
		}
	}

	@Override
	public Iterator<Property> getPropertyIterator() {
		return properties.values().iterator();
	}
	
	@Override
	public void setProperty(Property p) {
		if (p != null)
			this.properties.put(p.getNamespace() + p.getName(), p);
		lm = new Date();
	}

	@Override
	public Date getCreationDate() {
		return created;
	}

	@Override
	public Date getLastmodified() {
		return lm;
	}

	@Override
	public void remProperty(PropertyRef ref) {
		properties.remove(ref.getNs() + ref.getName());
	}

	@Override
	public ResourceType getResourceTypes() {
		return rType;
	}

}
