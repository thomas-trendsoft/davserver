package davserver.protocol;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
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
	public void handle(HttpRequest breq,HttpResponse resp,IRepository repos,DAVUrl url) throws DAVException {
		HttpEntityEnclosingRequest req;
		
		// check request
		if (!(breq instanceof HttpEntityEnclosingRequest)) {
			throw new DAVException(400,"no body");
		}
		req = (HttpEntityEnclosingRequest)breq;

		// check resource reference
		if (repos == null || url == null || url.getResref() == null) {
			throw new DAVException(404,"resource not found");
		}

		// read report body
		if (req.getEntity().getContentLength() > 0) {
			try {
				Document body = XMLParser.singleton().parseStream(req.getEntity().getContent());
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

		
	}
}
