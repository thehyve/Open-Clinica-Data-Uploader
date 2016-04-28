package nl.thehyve.ocdu.soap.SOAPRequestDecorators;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class listAllStudiesRequestDecorator implements SoapDecorator{

    public  void decorateBody(SOAPEnvelope envelope) throws Exception { //TODO: handle exception
        SOAPBody soapBody = envelope.getBody();
        soapBody.addChildElement("listAllRequest","v1");
    }

}
