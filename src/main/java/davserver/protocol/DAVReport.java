package davserver.protocol;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.protocol.auth.Session;
import davserver.protocol.xml.ListElement;
import davserver.repository.IRepository;
import davserver.repository.Resource;
import davserver.repository.cal.BaseCalDAVRepository;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.XMLParser;

/**
 * Report method implementation
 * 
 * @author tkrieger
 *
 */
public class DAVReport extends DAVRequest {
	
	/**
	 * debug flag
	 */
	private boolean debug;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVReport() {
		this.debug = true;
	}
	
	/**
	 * Handle report request
	 * 
	 * @param req
	 * @param resp
	 * @param repos
	 * @param url
	 * @throws DAVException
	 */
	public void handle(HttpRequest breq,HttpResponse resp,IRepository repos,DAVUrl url,Session session) throws DAVException {
		HttpEntityEnclosingRequest req;
		Document                   body = null;
		
		// check request
		if (!(breq instanceof HttpEntityEnclosingRequest)) {
			throw new DAVException(400,"no body");
		}
		req = (HttpEntityEnclosingRequest)breq;

		// check resource reference
		if (repos == null || url == null || url.getResref() == null) {
			throw new DAVException(404,"resource not found");
		}
		
		// check resource
		Resource r;
		try {
			r = repos.locate(url.getResref());
		} catch (NotFoundException e1) {
			throw new DAVException(404,"not found");
		} catch (NotAllowedException e1) {
			throw new DAVException(405,"not allowed");
		}
		if (r == null) {
			throw new DAVException(404,"not found");
		}
		
		// read report body
		if (req.getEntity().getContentLength() > 0) {
			try {
				body = XMLParser.singleton().parseStream(req.getEntity().getContent());
				System.out.println("got a report body: " + body);
				if (debug) { DAVUtil.debug(body); }
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
			throw new DAVException(400,"bad request");
		}
		
		Element root = body.getDocumentElement();
		if (root == null) {
			throw new DAVException(400,"bad request");
		}
		
		ListElement response = null;
		
		// check caldav special reports
		if (DAVServer.CalDAVNS.compareTo(root.getNamespaceURI())==0) {
			if (!(repos instanceof BaseCalDAVRepository)) {
				throw new DAVException(415, "unsupported media type");
			}
			BaseCalDAVRepository crepos = (BaseCalDAVRepository)repos;
			if (root.getLocalName().compareTo("calendar-multiget")==0) {
				System.out.println("report calendar-multiget");
				response = crepos.reportMultiGet(r,url,root);
			}
		}
	
		if (response == null) {
			throw new DAVException(415, "unsupported media type");
		}
		
		// MultiStatus Response
		try {
			this.respondXML(207, response.createDocument(),resp);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new DAVException(500,e.getMessage());
		}
		
	} // handle
	
}
