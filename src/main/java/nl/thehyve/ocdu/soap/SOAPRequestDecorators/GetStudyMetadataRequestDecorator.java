package nl.thehyve.ocdu.soap.SOAPRequestDecorators;

import nl.thehyve.ocdu.models.OCEntities.Study;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

/**
 * Created by piotrzakrzewski on 26/04/16.
 */
public class GetStudyMetadataRequestDecorator implements SoapDecorator {

    private Study study;

    public void setStudy(Study study) {
        this.study = study;
    }

    @Override
    public void decorateBody(SOAPEnvelope envelope) throws Exception {
        SOAPBody soapBody = envelope.getBody();
        SOAPElement getMetaDataRequest = soapBody.addChildElement("getMetadataRequest", "v1");
        SOAPElement studyMetadata = getMetaDataRequest.addChildElement("studyMetadata", "v1");
        SOAPElement identifier = studyMetadata.addChildElement("identifier", "beans");
        if (study == null) {
            throw new Exception("Cannot decorateBody of getStudyMetdata request without Study. Set study first.");
        }
        identifier.setTextContent(study.getIdentifier());
    }


}
