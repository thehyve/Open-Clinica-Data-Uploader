package nl.thehyve.ocdu.soap.SOAPRequestDecorators;

import nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
        Document odmContentDoc = SoapUtils.simpleString2XmlDoc(odm);
        Node odmRoot = importRequestElement.getOwnerDocument().importNode(odmContentDoc.getFirstChild(), true);

        importRequestElement.appendChild(odmRoot);
    }
}
