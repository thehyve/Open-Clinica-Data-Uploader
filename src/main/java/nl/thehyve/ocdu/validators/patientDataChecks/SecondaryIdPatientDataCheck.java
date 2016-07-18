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
public class SecondaryIdPatientDataCheck implements PatientDataCheck {

    public final static int MAX_SECONDARY_ID_LENGTH = 30;

    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData,
                                                        List<StudySubjectWithEventsType> subjectWithEventsTypes,
                                                        Set<String> ssidsInData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        String secondaryId = subject.getSecondaryId();

        if (!StringUtils.isBlank(secondaryId)) {
            if (secondaryId.length() > MAX_SECONDARY_ID_LENGTH) {
                error = new ValidationErrorMessage(commonMessage + "The length of secondary ID is over " + MAX_SECONDARY_ID_LENGTH + " characters.");
            }
        }

        if(error != null) {
            error.addOffendingValue("Secondary ID: " + subject.getSecondaryId());
        }

        return error;
    }
}
