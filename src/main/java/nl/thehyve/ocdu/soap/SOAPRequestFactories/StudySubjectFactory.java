package nl.thehyve.ocdu.soap.SOAPRequestFactories;

import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import org.openclinica.ws.beans.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static nl.thehyve.ocdu.soap.SOAPRequestFactories.StudyRefFactory.createStudyRef;

/**
 * Created by piotrzakrzewski on 17/06/16.
 */
public class StudySubjectFactory {

    public static StudySubjectType createStudySubject(Subject subject, Study study, SiteDefinition site) {
        StudySubjectType studySubjectType = new StudySubjectType();
        SubjectType subjectType = createSubjectType(subject);
        studySubjectType.setSubject(subjectType);
        studySubjectType.setLabel(subject.getSsid());
        StudyRefType studyRef = createStudyRef(study, site);
        studySubjectType.setStudyRef(studyRef);
        return studySubjectType;
    }

    public static SubjectType createSubjectType(Subject subject) {
        SubjectType subjectType = new SubjectType();
        //subjectType.setUniqueIdentifier("should this ever be set?"); //TODO: find out if this should be set
        if (subject.getDateOfBirth() != null && !subject.getDateOfBirth().equals("")) {
            XMLGregorianCalendar dateOfBirth = createXMLGregorianDate(subject.getDateOfBirth());
            subjectType.setDateOfBirth(dateOfBirth);
        }
        if (subject.getGender() != null && !subject.getGender().equals("")) {
            GenderType genderType = GenderType.fromValue(subject.getGender());
            subjectType.setGender(genderType);
        }
        return subjectType;
    }

    public static XMLGregorianCalendar createXMLGregorianDate(String dateOfBirthString) {
        try {
            XMLGregorianCalendar dateOfBirth = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateOfBirthString);
            return dateOfBirth;
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
