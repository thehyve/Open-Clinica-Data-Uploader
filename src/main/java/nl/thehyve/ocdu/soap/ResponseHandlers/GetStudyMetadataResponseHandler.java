package nl.thehyve.ocdu.soap.ResponseHandlers;

import nl.thehyve.ocdu.models.MetaData;
import nl.thehyve.ocdu.models.Study;
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
 * Created by piotrzakrzewski on 29/04/16.
 */
public class GetStudyMetadataResponseHandler extends OCResponseHandler {

    public static MetaData parseGetStudyMetadataResponse(SOAPMessage response) throws Exception { //TODO: handle exception
        Document document = toDocument(response);
        if (isAuthFailure(document)) {
            throw new AuthenticationCredentialsNotFoundException("Authentication against OpenClinica unsuccessfull");
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node odmNode = (Node) xpath.evaluate("//odm", document, XPathConstants.NODE);
        //TODO: parse metadata
        return null;
    }

}
