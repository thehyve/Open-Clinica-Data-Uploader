package nl.thehyve.ocdu.soap.SOAPRequestDecorators;

import javax.xml.soap.SOAPEnvelope;

/**
 * Created by piotrzakrzewski on 26/04/16.
 */
public interface SoapDecorator {

    void decorateBody(SOAPEnvelope envelope) throws Exception;

}
