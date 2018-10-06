package davserver.protocol.xml;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import davserver.DAVServer;

/**
 * Response http entry 
 * 
 * @author tkrieger
 *
 */
public class Response extends DAVXMLObject {

	/**
	 * Resource reference
	 */
	private String href;
	
	/**
	 * Http Status
	 */
	private int status;
	
	/**
	 * Child elements
	 */
	private List<DAVXMLObject> childs;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param href
	 */
	public Response(String href,int status) {
		this.href   = href;
		this.status = status;
		this.childs = new LinkedList<>();
	}
	
	public void addChild(DAVXMLObject child) {
		this.childs.add(child);
	}

	@Override
	public Element appendXML(Element root) {
		Element resp = root.getOwnerDocument().createElementNS(DAVServer.Namespace,"response");
		Element nref = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "href");
		Element stat = root.getOwnerDocument().createElementNS(DAVServer.Namespace, "status");
		
		if (status == 200) {
			stat.setTextContent("HTTP/1.1 OK 200");
		}
		nref.setTextContent(href);
		
		resp.appendChild(nref);
		for (DAVXMLObject c : childs) {
			c.appendXML(resp);
		}
		resp.appendChild(stat);
		
		return resp;
	}
	
}
