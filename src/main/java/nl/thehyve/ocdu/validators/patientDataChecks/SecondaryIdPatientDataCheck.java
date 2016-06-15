package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bo on 6/15/16.
 */
public class SecondaryIdPatientDataCheck implements PatientDataCheck{

    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        String secondaryId = subject.getSecondaryId();

        if(!StringUtils.isBlank(secondaryId)) {
            if(secondaryId.length() > 30) {
                error = new ValidationErrorMessage(commonMessage + "The length of secondary ID is over 30 characters.");
            }
        }

        return error;
    }
}
