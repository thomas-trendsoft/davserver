package davserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLParser {

	private static XMLParser master = null;
	
	private DocumentBuilderFactory factory;
	
	private ConcurrentLinkedQueue<DocumentBuilder> bpool;
	
	private XMLParser() {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		bpool   = new ConcurrentLinkedQueue<DocumentBuilder>();
	}
	
	public static XMLParser singleton() {
		if (master == null) 
			master = new XMLParser();
		return master;
	}
	
	private synchronized DocumentBuilder createBuilder() throws ParserConfigurationException {
		return factory.newDocumentBuilder();
	}
	
	private DocumentBuilder getBuilder() throws ParserConfigurationException {
		DocumentBuilder b = bpool.poll();
		if (b == null) {
			b = createBuilder();
		}
		return b;
	}
	
	private void releaseBuilder(DocumentBuilder b) {
		if (bpool.size() < 20)
			bpool.add(b);
	}
	
	/**
	 * Parse an Stream to an XML Document
	 * 
	 * @param in
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public Document parseStream(InputStream in) throws SAXException, IOException, ParserConfigurationException {
		Document        doc;
		DocumentBuilder b = getBuilder();
		doc = b.parse(in);
		releaseBuilder(b);
		return doc;
	}
	
	/**
	 * Create an empty xml document 
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 */
	public Document createDocument() throws ParserConfigurationException {
		Document        doc;
		DocumentBuilder b = getBuilder();
		doc = b.newDocument();
		releaseBuilder(b);
		return doc;		
	}
	
	/**
	 * Serialisieren eines XML Dokumentes zum String
	 * 
	 * @param doc
	 * @return
	 * @throws TransformerException
	 */
	public String serializeDoc(Document doc) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		//initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource    source = new DOMSource(doc);
		transformer.transform(source, result);

		String xmlString = result.getWriter().toString();
		return xmlString;		
	}
	
}
