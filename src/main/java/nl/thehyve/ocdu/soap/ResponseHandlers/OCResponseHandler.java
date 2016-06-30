package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
public class OCResponseHandler {


    public final static String authFailXpathExpr =  "//faultstring";


    /**
     * Checks if an error occurred in the call to OpenCLinica. Returns a empty String if no error occurred else it
     * returns the OpenClinica message.
     * @param xmlResponse
     * @return
     */
    public static String isAuthFailure(Document xmlResponse) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node faultNode;
        try {
            faultNode = (Node) xpath.evaluate(authFailXpathExpr,  //TODO: make it more specific, can we distinguish between different faultcodes?
                    xmlResponse, XPathConstants.NODE);
            if (faultNode == null) {
                return "";
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return e.getMessage(); // Do not proceed when auth status cannot be resolved.
        }
        return faultNode.getTextContent();
    }

    public static String parseGenericResponse(SOAPMessage response, String selector) throws Exception {
        Document document = toDocument(response);
        System.out.println("-->" + SoapUtils.soapMessageToString(response));
        if (! isAuthFailure(document).equals("")) {
            throw new AuthenticationCredentialsNotFoundException("Authentication against OpenClinica unsuccessfull");
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node importDataResponseNode = (Node) xpath.evaluate(selector, document, XPathConstants.NODE);
        Node resultNode = (Node) xpath.evaluate("//result", importDataResponseNode, XPathConstants.NODE);
        if ("fail".equalsIgnoreCase(resultNode.getTextContent())) {
            Node errorNode = (Node) xpath.evaluate("//error", importDataResponseNode, XPathConstants.NODE);
            return errorNode.getTextContent();
        }
        return null;
    }

}
