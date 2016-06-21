package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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

}
