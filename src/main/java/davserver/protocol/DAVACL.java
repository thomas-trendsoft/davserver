package davserver.protocol;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.pmw.tinylog.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.IRepository;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.XMLParser;

/**
 * ACL Implementation
 * 
 * @author tkrieger
 *
 */
public class DAVACL extends DAVRequest {

	@Override
	public void handle(HttpRequest breq, HttpResponse resp, IRepository repos, DAVUrl url)
			throws DAVException, NotFoundException, NotAllowedException {
		HttpEntityEnclosingRequest req;
		
		// check request
		if (!(breq instanceof HttpEntityEnclosingRequest)) {
			throw new DAVException(400,"no body");
		}
		req = (HttpEntityEnclosingRequest)breq;

		
		if (req.getEntity().getContentLength() > 0) {
			try {
				Document body = XMLParser.singleton().parseStream(req.getEntity().getContent());
				System.out.println("got a acl body: " + body);
				Logger.debug(DAVUtil.debug(body));
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
		}
	}

}
