package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class SoapUtils {

    public static Document toDocument(SOAPMessage soapMsg) //TODO: handle exception
            throws TransformerException, SOAPException, IOException, ParserConfigurationException, SAXException {
        final StringWriter sw = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(soapMsg.getSOAPPart()),
                new StreamResult(sw));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String xmlString = sw.toString();
        Document doc = builder.parse( new InputSource( new StringReader( xmlString ) ) );
        return doc;
    }

    public static Document unEscapeCDATAXML(String escapedXml) {
        //String xmlString = StringEscapeUtils.unescapeXml(escapedXml);
        Document doc = simpleString2XmlDoc(escapedXml);
        return doc;
    }

    public static Document simpleString2XmlDoc(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlString ) ) );
            return doc;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        Source xmlInput = new DOMSource(soapMessage.getSOAPPart());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StringResult stringResult = new StringResult();
        transformer.transform(xmlInput, stringResult);
        return stringResult.toString();
    }

    private static String escapeAttributeValues(String xmlString) {
        return null;
    }

}
