package davserver.protocol;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
import davserver.utils.XMLParser;

/**
 * HTTP LOCK Implementation class
 * 
 * @author tkrieger
 *
 */
public class DAVLock {

	private boolean debug;
	
	/**
	 * Defaultkonstruktor 
	 */
	public DAVLock() {
		this.debug = true;
	}
	

	/**
	 * Handle HTTP LOCK Method
	 * 
	 * @param req HTTP Request
	 * @param resp HTTP Response
	 * @param repos Repository
	 * @param r Resource
	 * @param url DAV URL
	 */
	public void handleLock(HttpEntityEnclosingRequest req,HttpResponse resp,IRepository repos,DAVUrl url) {
		// check if locks are supported
		if (!repos.supportLocks()) {
			DAVUtil.handleError(new DAVException(415,"Locks are not supported inside this repository"),resp);
			return;
		}
		// check lock info body
		if (req.getEntity().getContentLength() > 0) {
			try {
				Document body = XMLParser.singleton().parseStream(req.getEntity().getContent());
				System.out.println("got a lock body: " + body);
				if (debug) { DAVUtil.debug(body); }
				// read requested lock properties
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
			} 
		} else {
			DAVUtil.handleError(new DAVException(400,"No lock request body"),resp);
			return;
		}

	}
}
