package davserver.repository.cal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.protocol.xml.ElementIterator;
import davserver.protocol.xml.ListElement;
import davserver.protocol.xml.Response;
import davserver.repository.Collection;
import davserver.repository.IRepository;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;

/**
 * Base CalDAV requested methods implementation
 * 
 * @author tkrieger
 *
 */
public abstract class BaseCalDAVRepository implements IRepository {

	/**
	 * calendar-multiget implementation 
	 * 
	 * @param rres
	 * @param properties
	 * @param rlist
	 * @return
	 * @throws DAVException 
	 */
	public ListElement reportMultiGet(Resource rres,Element root) throws DAVException {
		List<PropertyRef> properties;
		List<URI>         rlist;
		ListElement       resp = new ListElement("multistatus", DAVServer.Namespace);
		Response          relem;
		
		// parse request
		rlist      = new LinkedList<>();
		properties = new LinkedList<>();
		ElementIterator citer = new ElementIterator(root);
		
		while (citer.hasNext()) {
			Element e = citer.next();
			if (DAVServer.Namespace.compareTo(e.getNamespaceURI())==0) {
				// parse property list
				if (e.getLocalName().compareTo("prop") == 0) {
					ElementIterator siter = new ElementIterator(e);
					while (siter.hasNext()) {
						Element pe = siter.next();
						properties.add(new PropertyRef(pe));
						System.out.println("add prop: " + pe.getLocalName());
					}
				// add href resource
				} else if (e.getLocalName().compareTo("href")==0) {
					try {
						URI url = new URI(e.getTextContent());
						rlist.add(url);
						System.out.println("add url: " + url);
					} catch (DOMException e1) {						
						e1.printStackTrace();
						throw new DAVException(500,e1.getMessage());
					} catch (URISyntaxException e1) {
						throw new DAVException(400,"bad request");
					}
				}
			}
		}
		
		// create response
		if (rres instanceof Collection) {
			System.out.println("check collection multi-get");
			for (URI url : rlist) {
				try {
					Resource r = this.locate(url.toString());
					if (r == null) {
						resp.addChild(new Response(url.toString(), 404));											
					} else {
						relem = new Response(url.toString(), 200);
						ListElement pstat = new ListElement("propstat", DAVServer.Namespace);
						relem.addChild(pstat);
						for (PropertyRef pr : properties) {
							Property p = r.getProperty(pr);
							if (p != null) {
								pstat.addChild(p);
							} else {
								System.out.println("check not found property response");
							}
						}
					}
				} catch (NotAllowedException e) {
					resp.addChild(new Response(url.toString(), 405));										
				} catch (NotFoundException e) {
					resp.addChild(new Response(url.toString(), 404));					
				} catch (Exception e) {
					e.printStackTrace();
					resp.addChild(new Response(url.toString(), 500));
				}
			}
		}
		
		return resp;
	}
	
}
