package nl.thehyve.ocdu.soap.SOAPRequestFactories;

import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import org.openclinica.ws.beans.*;
import org.openclinica.ws.studysubject.v1.CreateRequest;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import static nl.thehyve.ocdu.soap.SOAPRequestFactories.StudyRefFactory.createStudyRef;
import static nl.thehyve.ocdu.soap.SOAPRequestFactories.StudySubjectFactory.createStudySubject;

/**
 * Created by piotrzakrzewski on 17/06/16.
 */
public class CreateSubjectRequestFactory {
private static QName createRequestQname = new QName("http://openclinica.org/ws/studySubject/v1", "listAllByStudyRequest");

    public static Collection<JAXBElement<CreateRequest>> getCreateRequests(Collection<Subject> subjects, Study study, SiteDefinition site) {
        List<JAXBElement<CreateRequest>> createRequests = subjects.stream().map(subject -> {
            try {
                return getCreateRequest(subject, study, site);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
        return createRequests;
    }

    private static JAXBElement<CreateRequest> getCreateRequest(Subject subject, Study study, SiteDefinition site) throws DatatypeConfigurationException {
        CreateRequest request = new CreateRequest();
        StudySubjectType studySubject = createStudySubject(subject, study, site);
        request.setStudySubject(studySubject);
        JAXBElement<CreateRequest>  requestWrapped = new  JAXBElement<> (createRequestQname, CreateRequest.class, null, request);
        return requestWrapped;
    }

}
