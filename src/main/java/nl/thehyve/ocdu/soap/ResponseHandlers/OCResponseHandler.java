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

    public final static String authFailXpathExpr =  "//faultcode";

    public static boolean isAuthFailure(Document xmlResponse) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            Node faultNode = (Node) xpath.evaluate(authFailXpathExpr,  //TODO: make it more specific, can we distinguish between different faultcodes?
                    xmlResponse, XPathConstants.NODE);
            if (faultNode == null) {
                return false;
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return true; // Do not proceed when auth status cannot be resolved.
        }
        return true;
    }

}
