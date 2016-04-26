package nl.thehyve.ocdu.soap.SOAPRequestDecorators;

import javax.security.auth.Subject;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import java.util.List;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class createSubjectRequestDecorator {

    public  void decorateCreateSubjectRequestBody(SOAPEnvelope envelope, List<Subject> subjects) throws SOAPException {
        SOAPBody soapBody = envelope.getBody();
        //TODO: implement
        // soapBody.addChildElement("listAllRequest","v1");
    }

}
