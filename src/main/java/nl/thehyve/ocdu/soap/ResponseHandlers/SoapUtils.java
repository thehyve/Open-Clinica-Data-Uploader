package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public static XMLGregorianCalendar getFullXmlDate(String dateString) {
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            if (! StringUtils.isEmpty(dateString)) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = dateFormat.parse(dateString);
                calendar.setTime(date);
            }
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
