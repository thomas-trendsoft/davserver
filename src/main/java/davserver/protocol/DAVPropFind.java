package davserver.protocol;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.Collection;
import davserver.repository.IRepository;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.ResourceType;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.XMLParser;

/**
 * HTTP PROPFIND Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVPropFind {
	
	private boolean debug;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVPropFind() {
		this.debug = true;
	}
		
	/**
	 * Lists all Properties to an multistatus response 
	 * 
	 * @param multistatus
	 * @param r
	 * @param refs
	 * @param depth
	 */
	public void createPropFindResp(Element multistatus,DAVUrl rurl,Resource r,List<PropertyRef> refs,int depth) {
		HashSet<String> done = new HashSet<String>();
		Document       owner = multistatus.getOwnerDocument();
		// response root
		Element rres = owner.createElementNS(DAVServer.Namespace, "response");
		multistatus.appendChild(rres);

		// href for resource
		Element href = owner.createElementNS(DAVServer.Namespace,"href");
		href.setTextContent(rurl.getPrefix() + "/" + rurl.getRepository() + rurl.getResref());
		rres.appendChild(href);
		
		// propstat element
		Element propstat = owner.createElementNS(DAVServer.Namespace, "propstat");
		rres.appendChild(propstat);
		
		// iter property requests
		List<PropertyRef> notfound = new LinkedList<PropertyRef>();
		System.out.println("req pref count: " + refs.size());
		for (PropertyRef pr : refs) {
			// Named property list
			if (pr.getType() == PropertyRef.PROP) {
				for (PropertyRef spr : pr.getSubRefs()) {
					// check for default properties
					Property p = r.getProperty(spr);
					// check default properties
					if (p == null && spr.getNs().compareTo(DAVServer.Namespace) == 0) {
						if (spr.getName().compareTo("resourcetype") == 0) {
							if (r instanceof Collection)
								p = new ResourceType(DAVServer.Namespace,"collection");
							else 
								p = new ResourceType(null,null);
						} else if ((p = Property.getDAVProperty(spr, r)) == null) {
							System.out.println("UNKNOWN dav property: " + spr.getName());
						} 
					}
					if (p == null) {
						System.out.println("property not found: " + spr.getNs() + ":" + spr.getName());
						notfound.add(spr);
					} else {
						if (!done.contains(p.getNamespace() + ":" + p.getName())) {
							Element prop = owner.createElementNS(DAVServer.Namespace, "prop");
							propstat.appendChild(prop);
							prop.appendChild(p.toXML(owner));
							done.add(p.getNamespace() + ":" + p.getName());
						}
					}
				}
			} else if (pr.getType() == PropertyRef.ALLPROP) {
				Iterator<Property> piter = r.getPropertyIterator();
				while (piter.hasNext()) {
					Property p = piter.next();
					if (!done.contains(p.getNamespace() + ":" + p.getName())) {
						Element prop = owner.createElementNS(DAVServer.Namespace, "prop");
						propstat.appendChild(prop);
						prop.appendChild(p.toXML(owner));
						done.add(p.getNamespace() + ":" + p.getName());
					}
 				}
				
				// dav properties
				Property p;
				if (!done.contains(DAVServer.Namespace + ":resourcetype")) {
					if (r instanceof Collection)
						p = new ResourceType(DAVServer.Namespace,"collection");
					else 
						p = new ResourceType(null,null);					
					Element prop = owner.createElementNS(DAVServer.Namespace, "prop");
					propstat.appendChild(prop);
					prop.appendChild(p.toXML(owner));
					done.add(p.getNamespace() + ":" + p.getName());
				}
				
				for (String dpk : Property.getDAVProperties().keySet()) {
					if (!done.contains(dpk)) {
						p = Property.getDAVProperty(Property.getDAVProperties().get(dpk), r);
						if (p != null) {
							Element prop = owner.createElementNS(DAVServer.Namespace, "prop");
							propstat.appendChild(prop);
							prop.appendChild(p.toXML(owner));
							done.add(dpk);							
						}
					}					
				}
			}
		}
		
		// on collection with depth request append child resources
		if (depth > 0 && r instanceof Collection) {
			System.out.println("append child resources");
			Collection             c = (Collection)r;
			Iterator<Resource> riter = c.getChildIterator();
			String              base = rurl.getPrefix() + "/" + rurl.getRepository() + rurl.getResref();
			
			while (riter.hasNext()) {
				String   nu  = null;
				Resource cr  = riter.next();
				if (cr instanceof Collection) {
					nu = base + cr.getName() + "/";
				} else {
					nu = base + cr.getName();
				}
				DAVUrl curl = new DAVUrl(nu, rurl.getPrefix());
				createPropFindResp(multistatus, curl, cr, refs, 0);
			}
		}
		
	}
	
	/**
	 * Read the Property Request List
	 * 
	 * @param body
	 * @return
	 * @throws DAVException
	 */
	public List<PropertyRef> readPropFindReq(Element root,Resource r) throws DAVException {
		
		// check namespace and node name
		if (root.getNamespaceURI() == null || root.getNamespaceURI().compareTo(DAVServer.Namespace) != 0) {
			throw new DAVException(400,"Wrong namespace: " + root.getNamespaceURI());
		}
		if (root.getLocalName().compareTo("propfind")!=0) {
			throw new DAVException(400,"No propfind request body");
		}
		
		// iterate childs
		List<PropertyRef> ret = new LinkedList<PropertyRef>();
		Node child = root.getFirstChild();
		while (child != null) {
			if (child instanceof Element) {
				Element e = (Element)child;
				if (e.getNamespaceURI().compareTo(DAVServer.Namespace) == 0) {
					// Property List
					if (e.getLocalName().compareTo("prop")==0) {
						PropertyRef plist = new PropertyRef(PropertyRef.PROP);
						Node sr = e.getFirstChild();
						while (sr != null) {
							if (sr instanceof Element) {
								plist.getSubRefs().add(new PropertyRef((Element)sr));
							}
							sr = sr.getNextSibling();
						}
						ret.add(plist);
					} else if (e.getLocalName().compareTo("allprop")==0) {
						PropertyRef all = new PropertyRef(PropertyRef.ALLPROP);
						ret.add(all);
					} else {
						throw new DAVException(415,"Unsupported dav property: " + e.getNodeName());
					}
				} else {
					throw new DAVException(415,"Unsupported property request: " + e.getNamespaceURI() + ":" + e.getNodeName());
				}
			}
			child = child.getNextSibling();
		}
		
		return ret;
	}

	/**
	 * Mapping the PROPFIND Method to the repository class
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Given resource repository
	 * @param r Resource if found otherwithe null
	 */
	public void handlePropFind(HttpEntityEnclosingRequest req,HttpResponse resp,IRepository repos,DAVUrl durl) {
		Integer depthval;
		Header  depth;
		
		System.out.println("handle prop find");
		
		// check resource reference
		if (repos == null || durl == null || durl.getResref() == null) {
			DAVUtil.handleError(new DAVException(404,"resource not found"), resp);
			return;
		}
		
		// check resource entry
		Resource r = null;
		try {
			r = repos.locate(durl.getResref());			
		} catch (NotFoundException nfe) {
			DAVUtil.handleError(new DAVException(404,durl.getResref() + " not found"), resp);
			return;
		} catch (NotAllowedException nae) {
			DAVUtil.handleError(new DAVException(404,durl.getResref() + " not found"), resp);
			return;
		}
		
		// check if repos failed to say no resource
		if (r == null) {
			DAVUtil.handleError(new DAVException(404,durl.getResref() + " not found"), resp);
			return;			
		}
		
		// check for depth header value (infinity not supported as default now)
		depth = req.getFirstHeader("Depth");
		if (depth == null) {
			DAVUtil.handleError(new DAVException(400,"no depth header"),resp);
			return;
		} 
		
		try {
			depthval = Integer.parseInt(depth.getValue());			
		} catch (NumberFormatException nfe) {
			DAVUtil.handleError(new DAVException(400,"no depth number value"),resp);
			return;
		}
		
		if (depthval > 1 || depthval < 0) {
			DAVUtil.handleError(new DAVException(400,"wrong depth value: " + depthval),resp);
			return;
		}
		System.out.println("depth : " + depthval);
		
		// check for body if special properties or all (none body)
		List<PropertyRef> reflist = null;
		if (req.getEntity().getContentLength() > 0) {
			try {
				Document body = XMLParser.singleton().parseStream(req.getEntity().getContent());
				System.out.println("got a propfind body: " + body);
				if (debug) { DAVUtil.debug(body); }
				// read requested properties
				reflist = readPropFindReq(body.getDocumentElement(),r);
			} catch (SAXParseException pe) {
				DAVUtil.handleError(new DAVException(400,pe.getMessage()),resp);
				return;
			} catch (UnsupportedOperationException e) {
				DAVUtil.handleError(new DAVException(400,e.getMessage()),resp);
				return;
			} catch (SAXException e) {
				DAVUtil.handleError(new DAVException(400,e.getMessage()),resp);
				return;
			} catch (IOException e) {
				DAVUtil.handleError(new DAVException(500,e.getMessage()),resp);
				return;
			} catch (ParserConfigurationException e) {
				DAVUtil.handleError(new DAVException(500,e.getMessage()),resp);
				return;
			} catch (DAVException e) {
				DAVUtil.handleError(e,resp);
				e.printStackTrace();
				return;
			}
		} else {
			reflist = new LinkedList<PropertyRef>();
			reflist.add(new PropertyRef(PropertyRef.ALLPROP));
		}
		
		// Create response
		String xmlDoc = null;
		try {
			Document rdoc  = XMLParser.singleton().createDocument();
			Element  rroot = rdoc.createElementNS(DAVServer.Namespace, "multistatus");
			rdoc.appendChild(rroot);
			
			// Query requested properties
			createPropFindResp(rroot,durl, r, reflist, depthval);
			xmlDoc = XMLParser.singleton().serializeDoc(rdoc);
			
		} catch (Exception e) {
			e.printStackTrace();
			DAVUtil.handleError(new DAVException(500,e.getMessage()), resp);
			return;
		}
		
		// MultiStatus Response
		resp.setStatusCode(207);
		resp.setEntity(new StringEntity(xmlDoc, "utf-8"));
		System.out.println("RET PROPS:" + xmlDoc);
	}

}
