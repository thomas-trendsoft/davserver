package davserver;

import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import davserver.repository.Collection;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.ResourceType;
import davserver.repository.error.NotAllowedException;
import davserver.utils.XMLParser;

public class DAVUtil {
	
	/**
	 * Parse DAV Url form URI
	 * @param path
	 * @return
	 * @throws NotAllowedException 
	 */
	public static List<String> getPathComps(String path) throws NotAllowedException {
		List<String> comps = new LinkedList<String>();
		String[] ret = path.split("/");
		int i=0;
		if (ret.length > 0) {
			for (i=0;i<ret.length;i++) {
				if (!ret[i].trim().isEmpty()) {
					if (ret[i].trim().compareTo("..")==0) {
						if (comps.size() > 0) {
							comps.remove(comps.size()-1);
						} else {
							throw new NotAllowedException("no ref path found");
						}
					} else {
						comps.add(ret[i]);						
					}
				}
			}
		}
		return comps;
	}
	
	/**
	 * Help Method to send error messages
	 * 
	 * @param e DAV Exception
	 * @param resp HTTP Response
	 */
	public static void handleError(DAVException e,HttpResponse resp) {
		Document doc;
		Element  err;
		
		try {
			doc = XMLParser.singleton().createDocument();
			err = doc.createElementNS(DAVServer.Namespace, "error");
			doc.appendChild(err);
			err.setTextContent(e.getMessage());
			resp.setEntity(new StringEntity(XMLParser.singleton().serializeDoc(doc), "utf-8"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		resp.setStatusCode(e.getStatus());
	}
	
	/**
	 * Debugging Help method
	 * 
	 * @param doc
	 */
	public static void debug(Document doc) {
		try {
			System.out.println(XMLParser.singleton().serializeDoc(doc));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
}
