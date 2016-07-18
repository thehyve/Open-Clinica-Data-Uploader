package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Set;

/**
 * Created by bo on 6/15/16.
 */
public class PersonIdPatientDataCheck implements PatientDataCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData,
                                                        List<StudySubjectWithEventsType> subjectWithEventsTypes,
                                                        Set<String> ssidsInData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        String personId = subject.getPersonId();

        if (!StringUtils.isBlank(personId)) {
            error = new ValidationErrorMessage(commonMessage + "Person ID is not allowed during registration via web services. Please delete this column from your file or leave the fields empty ");
        }

        if(error != null) {
            error.addOffendingValue("Person ID: " + subject.getPersonId());
        }

        return error;
    }

}
