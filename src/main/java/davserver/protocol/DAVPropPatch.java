package davserver.protocol;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
import davserver.utils.XMLParser;

/**
 * HTTP PROPPATCH Implementation
 * 
 * @author tkrieger
 *
 */
public class DAVPropPatch {

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
	 */
	public void handlePropPatch(HttpEntityEnclosingRequest req,HttpResponse resp,IRepository repos,DAVUrl durl) {
		System.out.println("proppatch");
		
		if (req.getEntity().getContentLength() > 0) {
			try {
				Document body = XMLParser.singleton().parseStream(req.getEntity().getContent());
				if (debug) { DAVUtil.debug(body); }
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
		} 
	}
	
}
