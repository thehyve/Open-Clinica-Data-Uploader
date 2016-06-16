package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by bo on 6/7/16.
 */
public class GenderPatientDataCheck implements PatientDataCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        if (!StringUtils.isBlank(subject.getGender())) {
            if (!subject.getGender().equals("m") & !subject.getGender().equals("f")) {
                error = new ValidationErrorMessage(commonMessage + "Gender needs to be specified as m or f. ");
            }
        }

        return error;
    }
}
