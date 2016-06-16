package nl.thehyve.ocdu.soap.SOAPRequestDecorators;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

/**
 * Decorator for the <code>isStudySubject</code> OpenClinica WS-call. This call is needed to
 * retrieve the technical subject ID <code>SubjectOID</code>. This technical ID is used in all
 * SOAP-calls to identify the subject
 * Created by Jacob Rousseau on 15-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class IsStudySubjectRequestDecorator implements SoapDecorator {

    private String subjectLabel;

    private String studyName;

    public IsStudySubjectRequestDecorator(String subjectLabel, String studyName) {
        this.subjectLabel = subjectLabel;
        this.studyName = studyName;
    }


    public void decorateBody(SOAPEnvelope envelope) throws Exception {
        SOAPBody soapBody = envelope.getBody();
        SOAPElement getMetaDataRequest = soapBody.addChildElement("isStudySubjectRequest", "v1");
        SOAPElement studySubject = getMetaDataRequest.addChildElement("studySubject", "v1");
        SOAPElement label = studySubject.addChildElement("label", "beans");
        label.setTextContent(subjectLabel);
        SOAPElement studyRef = getMetaDataRequest.addChildElement("studyRef", "beans");
        studyRef.setTextContent(studyName);
    }
}
