package davserver.repository.properties;

import java.util.List;

import org.w3c.dom.Element;

import davserver.DAVServer;
import davserver.repository.Property;

public class SupportedReportSet extends Property {
	
	private List<Property> reports;

	public SupportedReportSet(List<Property> reports) {
		super(DAVServer.Namespace,"supported-report-set",null);
		this.reports = reports;
	}

	/**
	 * Create the xml presentation of this property 
	 * 
	 * @param doc
	 * @return
	 */
	@Override
	public Element appendXML(Element doc,boolean content) {
		Element elem = doc.getOwnerDocument().createElementNS(this.getNamespace(), this.getName());
		
		for (Property p : reports) {
			Element s = doc.getOwnerDocument().createElementNS(this.getNamespace(), "supported-report");
			p.appendXML(s);
			elem.appendChild(s);
		}
		
		doc.appendChild(elem);
		return elem;
	}
}
