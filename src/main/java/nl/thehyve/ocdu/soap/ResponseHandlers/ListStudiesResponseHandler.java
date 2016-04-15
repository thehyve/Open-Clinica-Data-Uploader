package nl.thehyve.ocdu.soap.ResponseHandlers;

import nl.thehyve.ocdu.models.Study;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class ListStudiesResponseHandler {

    public static List<Study> parseListStudiesResponse(SOAPMessage response) throws Exception {
        Iterator<SOAPElement> studies = response.getSOAPPart().getEnvelope().getBody().getChildElements(new QName("study"));
        List<Study> studiesParsed = new ArrayList<Study>();
        while (studies.hasNext()) {
            SOAPElement studyElement = studies.next();
            Study study = parseStudy(studyElement);
            studiesParsed.add(study);
        }
        return studiesParsed;
    }

    public static Study parseStudy(SOAPElement studyElement) {
        String identifier = getFirstChildByName(studyElement, "identifier").getTextContent();
        String oid = getFirstChildByName(studyElement, "oid").getTextContent();
        String name = getFirstChildByName(studyElement, "name").getTextContent();
        return new Study(identifier, oid, name);
    }

    public static SOAPElement getFirstChildByName(SOAPElement soapElement, String name) {
        Iterator<SOAPElement> childElements = soapElement.getChildElements(new QName(name));
        if (!childElements.hasNext()) {
            return null;
        } else {
            return childElements.next();
        }
    }

}
