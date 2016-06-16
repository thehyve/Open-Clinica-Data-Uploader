package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by Jacob Rousseau on 15-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class IsStudySubjectResponseHandler extends OCResponseHandler {

    /**
     * Retrieve the study subjects technical ID; <code>studuSubjectOID</code> in OpenClinica
     * terminology.
     * @param response the SOAP-response
     * @return <code>null</code> if the provided subject label does not exist in the study otherwise
     * the <code>studySubjectOID</code>
     * @throws Exception on authentication failures or response structure mismatch
     */
    public static String parseIsStudySubjectResponse(SOAPMessage response) throws Exception {
        if (response == null) {
            return null;
        }
        Document document = toDocument(response);
        if (isAuthFailure(document)) {
            throw new AuthenticationCredentialsNotFoundException("Authentication against OpenClinica unsuccessfull");
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node createResponseNode = (Node) xpath.evaluate("//createResponse", document, XPathConstants.NODE);
        Node resultNode = (Node) xpath.evaluate("//result", createResponseNode, XPathConstants.NODE);
        if ("Success".equals(resultNode.getTextContent())) {
            Node subjectOIDNode = (Node) xpath.evaluate("//subjectOID", createResponseNode, XPathConstants.NODE);
            if (subjectOIDNode != null) {
                return subjectOIDNode.getTextContent();
            }
            throw new IllegalStateException("SubjectOID node is null");
        }
        else {
            return null;
        }
    }
}
