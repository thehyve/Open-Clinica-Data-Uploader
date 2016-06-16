package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bo on 6/15/16.
 */
public class StudyPatientDataCheck implements PatientDataCheck {

    //checks both study and sites validity
    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        String study = subject.getStudy();

        if (StringUtils.isBlank(study)) {
            error = new ValidationErrorMessage(commonMessage + "Study should be provided.");
        } else {
            List<SiteDefinition> sites = metaData.getSiteDefinitions();
            List<String> sitenames = new ArrayList<>();
            for (SiteDefinition sd : sites) {
                sitenames.add(sd.getName());
            }
            if (!sitenames.contains(subject.getSite())) {
                error = new ValidationErrorMessage(commonMessage + "Study site \"" + subject.getSite() + "\" does not exist.");
            }
        }

        return error;
    }
}
