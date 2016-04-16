package nl.thehyve.ocdu.soap.ResponseHandlers;

import nl.thehyve.ocdu.models.Study;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.getFirstChildByName;
import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.getFirtsChildByName;
import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class ListStudiesResponseHandler {

    public static List<Study> parseListStudiesResponse(SOAPMessage response) throws Exception { //TODO: handle exception
        Document document = toDocument(response);

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList studyNodes = (NodeList) xpath.evaluate("//listAllResponse/studies/study", document, XPathConstants.NODESET);
        List<Study> studiesParsed = new ArrayList<>();
        for (int i = 0; i < studyNodes.getLength(); i++) {
            Node studyNode = studyNodes.item(i);
            Study study = parseStudy(studyNode);
            studiesParsed.add(study);
        }
        return studiesParsed;
    }

    public static Study parseStudy(Node studyElement) throws Exception { //TODO: handle exception
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node identifier = (Node) xpath.evaluate("./identifier", studyElement, XPathConstants.NODE);
        Node oid = (Node) xpath.evaluate("./oid", studyElement, XPathConstants.NODE);
        Node name = (Node) xpath.evaluate("./name", studyElement, XPathConstants.NODE);

        return new Study(identifier.getTextContent(), oid.getTextContent(), name.getTextContent());
    }




}
