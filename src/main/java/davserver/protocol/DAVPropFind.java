package davserver.protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.NStringEntity;
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
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.XMLParser;

/**
 * HTTP PROPFIND Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVPropFind extends DAVRequest {
	
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
	 * @throws DAVException 
	 * @returns a list of not found properties
	 */
	public void createPropFindResp(Element multistatus,DAVUrl rurl,Resource r,List<PropertyRef> refs,int depth,IRepository repos) throws DAVException {
		HashSet<String> done     = new HashSet<String>();
		Document        owner    = multistatus.getOwnerDocument();
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
						if (Property.getDAVProperties().containsKey(DAVServer.Namespace + spr.getName())) {
							p = Property.getDAVProperty(DAVServer.Namespace + spr.getName(), r, repos,rurl);
						} else {
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
							p.appendXML(prop);
							done.add(p.getNamespace() + ":" + p.getName());
						}
					}
				}
			} else if (pr.getType() == PropertyRef.ALLPROP || pr.getType() == PropertyRef.PROPNAMES) {
				boolean content = pr.getType() != PropertyRef.PROPNAMES;
				Iterator<Property> piter = r.getPropertyIterator();
				while (piter.hasNext()) {
					Property p = piter.next();
					if (!done.contains(p.getNamespace() + ":" + p.getName())) {
						Element prop = owner.createElementNS(DAVServer.Namespace, "prop");
						propstat.appendChild(prop);
						p.appendXML(prop,content);
						done.add(p.getNamespace() + ":" + p.getName());
					}
 				}
				
				// add default properties
				for (String dpk : Property.getDAVProperties().keySet()) {
					if (!done.contains(dpk)) {
						Property p = Property.getDAVProperty(dpk, r, repos, rurl);
						if (p != null) {
							Element prop = owner.createElementNS(DAVServer.Namespace, "prop");
							propstat.appendChild(prop);
							p.appendXML(prop,content);
							done.add(dpk);							
						}
					}					
				}
			} 
		}
		
		// append http ok stat
		Element hnode = owner.createElementNS(DAVServer.Namespace, "status");
		hnode.setTextContent("HTTP/1.1 200 OK");
		propstat.appendChild(hnode);
		
		// append not found status
		if (!notfound.isEmpty()) {
			Element nfps   = owner.createElementNS(DAVServer.Namespace, "propstat");
			Element pfn = owner.createElementNS(DAVServer.Namespace, "prop");
			rres.appendChild(nfps);
			nfps.appendChild(pfn);
			for (PropertyRef pf : notfound) {
				Element pe = owner.createElementNS(pf.getNs(),pf.getName());
				pfn.appendChild(pe);
			}
			Element nfref = owner.createElementNS(DAVServer.Namespace, "href");
			nfref.setTextContent(rurl.toString());
			nfps.appendChild(nfref);			
			Element nfnode = owner.createElementNS(DAVServer.Namespace, "status");
			nfnode.setTextContent("HTTP/1.1 404 Not Found");
			nfps.appendChild(nfnode);			
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
				createPropFindResp(multistatus, curl, cr, refs, 0, repos);
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
					} else if (e.getLocalName().compareTo("include")==0) {
						PropertyRef pr;
						Node sr = e.getFirstChild();
						while (sr != null) {
							if (sr instanceof Element) {
								pr = new PropertyRef((Element)sr);
								ret.add(pr);
							}
							sr = sr.getNextSibling();
						}
					} else if (e.getLocalName().compareTo("propname")==0) {
						PropertyRef pn = new PropertyRef(PropertyRef.PROPNAMES);
						ret.add(pn);
					} else {
						PropertyRef pr = new PropertyRef(e);
						ret.add(pr);
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
	 * @throws NotFoundException 
	 */
	public void handle(HttpRequest breq,HttpResponse resp,IRepository repos,DAVUrl durl) throws DAVException, NotFoundException {
		Integer depthval;
		Header  depth;
		HttpEntityEnclosingRequest req;
		
		System.out.println("handle prop find: " + durl.getResref());
		
		// check request
		if (!(breq instanceof HttpEntityEnclosingRequest)) {
			throw new DAVException(400,"no body");
		}
		req = (HttpEntityEnclosingRequest)breq;
		
		// check resource reference
		if (repos == null || durl == null || durl.getResref() == null) {
			throw new DAVException(404,"resource not found");
		}
		
		// check resource entry
		Resource r = null;
		try {
			r = repos.locate(durl.getResref());			
		} catch (NotAllowedException nae) {
			throw new DAVException(404,durl.getResref() + " not found");
		}
		
		// check if repos failed to say no resource
		if (r == null) {
			throw new DAVException(404,durl.getResref() + " not found");
		}
		
		// check for depth header value (infinity not supported as default now)
		depth = req.getFirstHeader("Depth");
		if (depth == null || depth.getValue().compareTo("infinity")==0) {
			depthval = Integer.MAX_VALUE;
		} else {
			try {
				depthval = Integer.parseInt(depth.getValue());			
			} catch (NumberFormatException nfe) {
				throw new DAVException(400,"no depth number value");
			}			
		}
		
		
		if (depthval < 0) {
			throw new DAVException(400,"wrong depth value: " + depthval);
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
				throw new DAVException(400,pe.getMessage());
			} catch (UnsupportedOperationException e) {
				throw new DAVException(400,e.getMessage());
			} catch (SAXException e) {
				throw new DAVException(400,e.getMessage());
			} catch (IOException e) {
				throw new DAVException(500,e.getMessage());
			} catch (ParserConfigurationException e) {
				throw new DAVException(500,e.getMessage());
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
			createPropFindResp(rroot,durl, r, reflist, depthval, repos);
			
			xmlDoc = XMLParser.singleton().serializeDoc(rdoc);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		}
		
		// MultiStatus Response
		try {
			resp.setStatusCode(207);
			resp.setEntity(new NStringEntity(xmlDoc, "utf-8"));
			resp.setHeader("Content-Type","application/xml;charset=utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		}

		if (debug)
			System.out.println("RET PROPS:" + xmlDoc);
	}

}
