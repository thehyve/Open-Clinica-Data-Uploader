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
    private static QName createRequestQname = new QName("http://openclinica.org/ws/studySubject/v1", "createRequest");

    public static JAXBElement<CreateRequest> getCreateRequests(Subject subject) {
        try {
            Study study = new Study(subject.getStudy(), subject.getStudy(), subject.getStudy());  //TODO: check if it needs to be an identifier or a name
            SiteDefinition site = new SiteDefinition();
            String siteText = subject.getSite();
            if (siteText != null && !siteText.equals("")) {
                site.setSiteOID(siteText); //TODO: We need to get an OID here and not name? Probably we need to use metadata here to get correct site
            } else {
                site = null;
            }
            return getCreateRequest(subject, study, site);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JAXBElement<CreateRequest> getCreateRequest(Subject subject, Study study, SiteDefinition site) throws DatatypeConfigurationException {
        CreateRequest request = new CreateRequest();
        StudySubjectType studySubject = createStudySubject(subject, study, site);
        request.setStudySubject(studySubject);
        JAXBElement<CreateRequest> requestWrapped = new JAXBElement<>(createRequestQname, CreateRequest.class, null, request);
        return requestWrapped;
    }

}
