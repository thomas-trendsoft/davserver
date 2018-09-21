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

import davserver.DAVServer;
import davserver.DAVUtil;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;

public class SimpleResource extends Resource {
	
	private HashMap<String,Property> properties;
	
	private String content;

	private Date lm;
	
	private Date created;
	
	
	public SimpleResource(String name) {
		super(name);

		lm = new Date();
		created = new Date();

		properties = new HashMap<String,Property>();
		properties.put("getlastmodified", new Property(DAVServer.Namespace,"getlastmodified",new Date(0)));
		properties.put("creationdate",new Property(DAVServer.Namespace,"creationdate",new Date(0)));
	}

	@Override
	public Property getProperty(PropertyRef ref) {
		System.out.println("get property: " + ref.getName());
		return properties.get(ref.getName());
	}
	
	public void setContent(String str) {
		this.content = str;
		this.lm      = new Date();
	}
	
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(content.getBytes("utf-8"));
	}

	@Override
	public int getContentLength() {
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
			this.properties.put(p.getName(), p);
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


}
