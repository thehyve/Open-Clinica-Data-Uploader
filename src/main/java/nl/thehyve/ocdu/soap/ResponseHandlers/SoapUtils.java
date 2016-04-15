package nl.thehyve.ocdu.soap.ResponseHandlers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import java.util.Iterator;

/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class SoapUtils {

    public static SOAPElement getFirstChildByName(SOAPElement soapElement, String name) {
        Iterator<SOAPElement> childElements = soapElement.getChildElements(new QName(name));
        if (!childElements.hasNext()) {
            return null;
        } else {
            return childElements.next();
        }
    }

}
