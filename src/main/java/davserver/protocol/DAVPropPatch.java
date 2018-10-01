package davserver.protocol;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.NStringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.protocol.xml.ListElement;
import davserver.repository.IRepository;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.XMLParser;

/**
 * HTTP PROPPATCH Implementation
 * 
 * @author tkrieger
 *
 */
public class DAVPropPatch extends DAVRequest {

	/**
	 * Debugging flag
	 */
	private boolean debug;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVPropPatch() {
		this.debug = true;
	}
	
	/**
	 * Handle PropPatch Implementation
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param durl Resource URL
	 * @throws DAVException 
	 * @throws NotAllowedException 
	 * @throws NotFoundException 
	 */
	public void handle(HttpRequest breq,HttpResponse resp,IRepository repos,DAVUrl durl) throws DAVException, NotFoundException, NotAllowedException {
		Resource target;
		Document body;
		HttpEntityEnclosingRequest req;
		
		// check request
		if (!(breq instanceof HttpEntityEnclosingRequest)) {
			throw new DAVException(400,"no body");
		}
		req = (HttpEntityEnclosingRequest)breq;

		// check precondition
		DAVRequest.checkLock(req, repos, durl);

		// check target resource
		target = repos.locate(durl.getResref());
		if (target == null) {
			throw new DAVException(404,"not found");
		}

		// check patch body 
		if (req.getEntity().getContentLength() > 0) {
			try {
				body = XMLParser.singleton().parseStream(req.getEntity().getContent());
				if (body == null) 
					throw new DAVException(400,"xml error");
				if (debug) { DAVUtil.debug(body); }
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
			throw new DAVException(400,"bad request");
		}
		
		try {
			// prepare response 
			ListElement mstat = new ListElement("multistatus", DAVServer.Namespace);
			ListElement mresp  = new ListElement("response",DAVServer.Namespace);
			mresp.addChild(new Property(DAVServer.Namespace, "href", durl.toString()));
			
			ListElement nfstat = new ListElement("propstat",DAVServer.Namespace);
			ListElement nflist = new ListElement("prop",DAVServer.Namespace);
			nfstat.addChild(nflist);
			
			// init status flag
			boolean notallowed = false;
			
			// parse request body
			Node root = body.getFirstChild();
			if (root.getNamespaceURI().compareTo(DAVServer.Namespace)==0 && root.getLocalName().compareTo("propertyupdate")==0) {
				// search set elements
				Node child = root.getFirstChild();
				while (child != null) {
					if (child instanceof Element) {
						if (child.getNamespaceURI().compareTo(DAVServer.Namespace)==0) {
							if (child.getLocalName().compareTo("set")==0) {
								// search prop elements
								Node pchild = child.getFirstChild();
								while (pchild != null) {
									if (pchild instanceof Element && pchild.getNamespaceURI().compareTo(DAVServer.Namespace)==0 && pchild.getLocalName().compareTo("prop")==0) {
										Node vchild = pchild.getFirstChild();
										while (vchild != null) {
											// first element as value
											if (vchild instanceof Element) {
												Property p = new Property((Element)vchild);
												try {
													target.setProperty(p);
												} catch (NotAllowedException e) {
													notallowed = true;
													nflist.addChild(p);
												}
											}
											vchild = vchild.getNextSibling();
										}
									}
									pchild = pchild.getNextSibling();
								} // prop element
							} else if (child.getLocalName().compareTo("remove")==0) {
								Node pchild = child.getFirstChild();
								while (pchild != null) {
									if (pchild instanceof Element && pchild.getNamespaceURI().compareTo(DAVServer.Namespace)==0 && pchild.getLocalName().compareTo("prop")==0) {
										Node vchild = pchild.getFirstChild();
										while (vchild != null) {
											// first element as value
											if (vchild instanceof Element) {
												PropertyRef pr = new PropertyRef((Element)vchild);
												target.remProperty(pr);
											}
											vchild = vchild.getNextSibling();
										}
									}
									pchild = pchild.getNextSibling();
								}
							}
						}  
					} 
					child = child.getNextSibling();
				}
			} else {
				throw new DAVException(400,"bad request");
			}
			
			// check not found status list
			if (notallowed) {
				mresp.addChild(nfstat);
			}
			
			String xmlDoc = XMLParser.singleton().serializeDoc(mstat.createDocument());
			resp.setStatusCode(207);
			resp.setEntity(new NStringEntity(xmlDoc, "utf-8"));
			resp.setHeader("Content-Type","application/xml;charset=utf-8");

			if (debug)
				System.out.println("RET PROPPATCH:" + xmlDoc);

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		}
		

	}
	
}
