package davserver.repository.cal;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUrl;
import davserver.protocol.xml.ElementIterator;
import davserver.protocol.xml.ListElement;
import davserver.protocol.xml.Response;
import davserver.repository.IRepository;
import davserver.repository.Property;
import davserver.repository.PropertyRef;
import davserver.repository.Resource;
import davserver.repository.cal.resource.CalDAVResource;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.XMLParser;
import ical4dav.properties.iCalComponent;

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
	public ListElement reportMultiGet(Resource rres,DAVUrl href,Element root) throws DAVException {
		List<PropertyRef> properties;
		List<URI>         rlist;
		ListElement       resp = new ListElement("multistatus", DAVServer.Namespace);
		Response          relem;
		
		System.out.println("caldav multi get");
		
		// precondition
		if (!(rres instanceof VCalendar)) {
			throw new DAVException(412, "wrong target resource type");
		}
		
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
		System.out.println("check collection multi-get");
		List<iCalComponent> comps = new LinkedList<>();
		try {
			relem = new Response(href.getURI(),207);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new DAVException(500,"server error");
		}
		
		for (URI url : rlist) {
			try {
				DAVUrl turl = new DAVUrl(url.toString(), href.getPrefix());
				Resource r = this.locate(turl.getResref());
				if (r == null) {
					resp.addChild(new Response(url.toString(), 404));
				} else if (r instanceof CalDAVResource) {
					comps.add(((CalDAVResource)r).getComponent());
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
		
		System.out.println("added comps: " + comps.size() + ":" + relem);
		
		VCalendar vcal = (VCalendar)rres;
		for (PropertyRef p : properties) {
			// special ical property 
			if (DAVServer.CalDAVNS.compareTo(p.getNs())==0 && ("calendar-data".compareTo(p.getName())==0)) {
				Property cd = new Property(DAVServer.CalDAVNS,"calendar-data",vcal.getCalendar().toString(comps));
				relem.addChild(cd);
			} else {
				Property prop = vcal.getProperty(p);
				if (prop != null)
					relem.addChild(prop);
			}
		}
		
		resp.addChild(relem);
		
		try {
			System.out.println("resp report: " + XMLParser.singleton().serializeDoc(resp.createDocument()));
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		
		return resp;
	}
	
}
