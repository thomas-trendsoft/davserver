package davserver.protocol;

import java.io.IOException;
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

public class DAVPropFind {
	
	private boolean debug;
	
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
	public static void createPropFindResp(Element multistatus,Resource r,List<PropertyRef> refs,int depth) {
		Element rres = multistatus.getOwnerDocument().createElementNS(DAVServer.Namespace, "response");
		multistatus.appendChild(rres);
		
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
						} 
					}
					if (p == null) {
						System.out.println("property not found: " + spr.getNs() + ":" + spr.getName());
						notfound.add(spr);
					} else {
						rres.appendChild(p.toXML(multistatus.getOwnerDocument()));
					}
				}
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
	public static List<PropertyRef> readPropFindReq(Element root) throws DAVException {
		
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
				reflist = readPropFindReq(body.getDocumentElement());
			} catch (UnsupportedOperationException e) {
				resp.setStatusCode(400);
				e.printStackTrace();
			} catch (SAXException e) {
				resp.setStatusCode(400);
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				resp.setStatusCode(500);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				resp.setStatusCode(500);
			} catch (DAVException e) {
				DAVUtil.handleError(e,resp);
				e.printStackTrace();
				return;
			}
		} else {
			DAVUtil.handleError(new DAVException(415,"All propfind not supported now"),resp);
			return;
		}
		
		// Create response
		String xmlDoc = null;
		try {
			Document rdoc  = XMLParser.singleton().createDocument();
			Element  rroot = rdoc.createElementNS(DAVServer.Namespace, "multistatus");
			rdoc.appendChild(rroot);
			
			// Query requested properties
			createPropFindResp(rroot, r, reflist, depthval);
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
