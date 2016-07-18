package nl.thehyve.ocdu.validators;


import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.patientDataChecks.*;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by bo on 6/7/16.
 */
public class PatientDataOcChecks {

    private final List<Subject> subjects;
    private final MetaData metadata;
    private final List<StudySubjectWithEventsType> subjEventData;
    private final Set<String> ssidsInData;

    private List<PatientDataCheck> checks = new ArrayList<>();

    public PatientDataOcChecks(MetaData metadata, List<Subject> subjects, List<StudySubjectWithEventsType> subjectWithEventsTypes,
                               Set<String> ssidsInData) {
        this.metadata = metadata;
        this.subjects = subjects;
        this.subjEventData = subjectWithEventsTypes;
        this.ssidsInData = ssidsInData;
        checks.add(new GenderPatientDataCheck());
        checks.add(new DateOfBirthPatientDataCheck());
        checks.add(new PersonIdPatientDataCheck());
        checks.add(new DateOfEnrollmentPatientDataCheck());
        checks.add(new SecondaryIdPatientDataCheck());
        checks.add(new StudyPatientDataCheck());
        checks.add(new SitePatientDataCheck());
        checks.add(new SubjectNotRegistered());
        checks.add(new PresentInData());
    }

    public List<ValidationErrorMessage> getErrors() {
        List<String> ssids = new ArrayList<>();
        List<ValidationErrorMessage> errors = new ArrayList<>();
        int index = 1;
        for (Subject subject : subjects) {
            String ssid = subject.getSsid();
            if (ssids.contains(ssid)) {
                ValidationErrorMessage error = new ValidationErrorMessage("Line " + index + " Study Subject ID: " + ssid + " duplicate is found.");
                error.addOffendingValue("ssid = " + ssid);
                errors.add(error);
            }
            for (PatientDataCheck check : checks) {
                ValidationErrorMessage error = check.getCorrespondingError(index, subject, metadata, subjEventData,
                        ssidsInData);
                if (error != null) {
                    errors.add(error);
                }
            }
            index++;
        }
        return errors;
    }

}
