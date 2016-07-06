package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bo on 6/15/16.
 */
public class StudyPatientDataCheck implements PatientDataCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        String study = subject.getStudy();
        String mStudy = metaData.getStudyName();

        if (StringUtils.isBlank(study)) {
            error = new ValidationErrorMessage(commonMessage + "Study should be provided.");
            error.addOffendingValue("Study: " + subject.getStudy());
        }
        else if(!study.equals(mStudy)) {
            error = new ValidationErrorMessage(commonMessage + "Study provided in the template is inconsistent with the study defined in the data file.");
            error.addOffendingValue("Study in template: " + study + ", study in data file: " + mStudy);
        }

        return error;
    }
}
