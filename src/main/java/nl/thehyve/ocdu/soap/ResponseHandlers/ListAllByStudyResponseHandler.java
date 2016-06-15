package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.studysubject.v1.CreateResponse;
import org.openclinica.ws.studysubject.v1.ListAllByStudyResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPMessage;
import java.util.List;

/**
 * SOAP response handler to handle the listAllByStudy operation of StudySubject-webservice.
 * Created by jacob on 6/2/16.
 */
public class ListAllByStudyResponseHandler {

    public static List<StudySubjectWithEventsType> retrieveStudySubjectsType(SOAPMessage soapMessage) throws Exception {
        JAXBContext jaxbContext  = JAXBContext.newInstance(ListAllByStudyResponse.class, CreateResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ListAllByStudyResponse ret = (ListAllByStudyResponse) unmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument());
        return ret.getStudySubjects().getStudySubject();
    }

}
