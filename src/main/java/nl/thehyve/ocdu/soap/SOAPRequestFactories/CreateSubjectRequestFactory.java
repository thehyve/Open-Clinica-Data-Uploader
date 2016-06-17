package nl.thehyve.ocdu.soap.SOAPRequestFactories;

import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import org.openclinica.ws.beans.SiteRefType;
import org.openclinica.ws.beans.StudyRefType;
import org.openclinica.ws.beans.StudySubjectType;
import org.openclinica.ws.beans.SubjectType;
import org.openclinica.ws.studysubject.v1.CreateRequest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import static nl.thehyve.ocdu.soap.SOAPRequestFactories.StudyRefFactory.createStudyRef;
import static nl.thehyve.ocdu.soap.SOAPRequestFactories.StudySubjectFactory.createStudySubject;

/**
 * Created by piotrzakrzewski on 17/06/16.
 */
public class CreateSubjectRequestFactory {


    public static Collection<CreateRequest> getCreateRequests(Collection<Subject> subjects, Study study, SiteDefinition site) {
        List<CreateRequest> createRequests = subjects.stream().map(subject -> {
            try {
                return getCreateRequest(subject, study, site);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
        return createRequests;
    }

    private static CreateRequest getCreateRequest(Subject subject, Study study, SiteDefinition site) throws DatatypeConfigurationException {
        CreateRequest request = new CreateRequest();
        StudySubjectType studySubject = createStudySubject(subject, study, site);
        request.setStudySubject(studySubject);
        return request;
    }

}
