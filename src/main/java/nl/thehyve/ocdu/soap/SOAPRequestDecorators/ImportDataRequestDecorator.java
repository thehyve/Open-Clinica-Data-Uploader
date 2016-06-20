package nl.thehyve.ocdu.soap.SOAPRequestDecorators;

import org.w3c.dom.CDATASection;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

/**
 * Created by Jacob Rousseau on 16-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class ImportDataRequestDecorator implements SoapDecorator {

    private String odm;

    public ImportDataRequestDecorator(String odm) {
        this.odm = odm;
    }


    public void decorateBody(SOAPEnvelope envelope) throws Exception {
        SOAPBody soapBody = envelope.getBody();
        SOAPElement importRequestElement = soapBody.addChildElement("importRequest", "v1");
        SOAPElement odmElement = importRequestElement.addChildElement("odm");
        CDATASection odmCData  = soapBody.getOwnerDocument().createCDATASection(odm);
        odmElement.appendChild(odmCData);
    }
}
