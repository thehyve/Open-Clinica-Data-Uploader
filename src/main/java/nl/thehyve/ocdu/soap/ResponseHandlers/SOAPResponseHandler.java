package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by Jacob Rousseau on 16-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class SOAPResponseHandler extends OCResponseHandler {

    /**
     * Checks if an error occurred on the OpenClinica-side and reports it back as the
     * return value
     *
     * @param response the SOAP-response.
     * @return a non <code>null</code> error code.message if an error occurred. Some are reported by the OpenClinica-WS
     * instance at url. Returns <code>null</code> if everything went OK.
     * @throws Exception if a technical error occurs.
     */

    public static String parseOpenClinicaResponse(SOAPMessage response, String xPathToResponse) throws Exception {
        Document document = toDocument(response);
        String result = isAuthFailure(document);
        if (! StringUtils.isEmpty(result)) {
            throw new AuthenticationCredentialsNotFoundException("Problem calling OpenClinica web-services: " + result);
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node importDataResponseNode = (Node) xpath.evaluate(xPathToResponse, document, XPathConstants.NODE);
        Node resultNode = (Node) xpath.evaluate("//result", importDataResponseNode, XPathConstants.NODE);
        if ("fail".equalsIgnoreCase(resultNode.getTextContent())) {
            Node errorNode = (Node) xpath.evaluate("//error", importDataResponseNode, XPathConstants.NODE);
            return errorNode.getTextContent();
        }
        return null;
    }

}
