package davserver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import davserver.repository.error.NotAllowedException;
import davserver.utils.XMLParser;

/**
 * Helping class for some protocol support 
 * 
 * @author tkrieger
 *
 */
public class DAVUtil {
	
	/**
	 * Helping method to create ETags 
	 * 
	 * @param data
	 * @throws NoSuchAlgorithmException
	 */
	public static String createHash(String data) throws NoSuchAlgorithmException {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(data.getBytes());
	    byte[] digest = md.digest();
	    return hex2String(digest);
	}
	
    /**
     * Hilfsmethode zur Umwandlung eines Byte-Arrays in ein String (hashes etc.)
     * 
     * @param data
     * @return Den data-Parameter als Hex-String
     */
    public static String hex2String(byte[] data)
    {
            String out,t;
            int    c;
            
            out = "";
            for (c=0;c<data.length;c++)
            {
                    t = Integer.toHexString(data[c] & 0xFF);
                    if (t.length() < 2)
                            t = "0" + t;
                    out += t;
            }
            return out;
    }

	
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
