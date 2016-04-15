package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class SoapUtils {

    public static SOAPElement getFirstChildByName(SOAPElement soapElement, String name) {
        Iterator<SOAPElement> childElements = soapElement.getChildElements(new QName(name));
        if (!childElements.hasNext()) {
            return null;
        } else {
            return childElements.next();
        }
    }

    public static SOAPElement getFirtsChildByName(SOAPBody body, String name) {
        Iterator<Node> nodes = body.getChildElements();
        while (nodes.hasNext()) {
            Node next = nodes.next();
            if (next.getNodeType() == Node.ELEMENT_NODE) {
                SOAPElement el = (SOAPElement) next;
                if (el.getElementName().getLocalName() == name) return el;
            }
        }
        return null;
    }

    public static Document toDocument(SOAPMessage soapMsg)
            throws TransformerException, SOAPException, IOException, ParserConfigurationException, SAXException {
        final StringWriter sw = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(soapMsg.getSOAPPart()),
                new StreamResult(sw));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String xmlString = sw.toString();
        Document doc = builder.parse( new InputSource( new StringReader( xmlString ) ) );
        System.out.println("Children:" + doc.getChildNodes().getLength());
        return doc;
    }

}
