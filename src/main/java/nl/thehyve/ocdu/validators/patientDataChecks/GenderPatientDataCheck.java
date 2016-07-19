package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Set;

/**
 * Created by bo on 6/7/16.
 */
public class GenderPatientDataCheck implements PatientDataCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData,
                                                        List<StudySubjectWithEventsType> subjectWithEventsTypes,
                                                        Set<String> ssidsInData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        boolean isGenderRequired = metaData.isGenderRequired();
        if (!isGenderRequired) {
            for (SiteDefinition sd : metaData.getSiteDefinitions()) {
                if (sd.isGenderRequired()) {
                    isGenderRequired = sd.isGenderRequired();
                    break;
                }
            }
        }


        ValidationErrorMessage error = null;
        if (!isGenderRequired && !StringUtils.isBlank(subject.getGender())) {
            error = new ValidationErrorMessage(commonMessage + " It is not allowed to upload gender by the study protocol");
        } else if (isGenderRequired) {
            if(subject.getGender() == null || StringUtils.isBlank(subject.getGender()) ) {
                error = new ValidationErrorMessage(commonMessage + "Gender needs to be specified as m or f. ");
            }
            else if(!subject.getGender().equals("m") && !subject.getGender().equals("f")) {
                error = new ValidationErrorMessage(commonMessage + "Gender needs to be specified as m or f. ");
            }
        }

        if (error != null) {
            error.addOffendingValue("Gender: " + subject.getGender());
        }

        return error;
    }
}
