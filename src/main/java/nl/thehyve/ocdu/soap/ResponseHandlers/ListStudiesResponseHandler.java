package nl.thehyve.ocdu.soap.ResponseHandlers;

import nl.thehyve.ocdu.models.OCEntities.Study;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class ListStudiesResponseHandler extends OCResponseHandler{

    public static List<Study> parseListStudiesResponse(SOAPMessage response) throws Exception { //TODO: handle exception
        Document document = toDocument(response);
        if (isAuthFailure(document)) {
            throw new AuthenticationCredentialsNotFoundException("Authentication against OpenClinica unsuccessfull");
        }
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
