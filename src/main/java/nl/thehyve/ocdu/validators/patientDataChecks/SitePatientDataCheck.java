package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by bo on 6/16/16.
 */
public class SitePatientDataCheck implements PatientDataCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData,
                                                        List<StudySubjectWithEventsType> subjectWithEventsTypes,
                                                        Set<String> ssidsInData) {

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        String study = subject.getStudy();
        String site = subject.getSite();

        if (!StringUtils.isBlank(site) && !StringUtils.isBlank(study)) {
            List<SiteDefinition> sites = metaData.getSiteDefinitions();
            if (sites == null) {
                error = new ValidationErrorMessage(commonMessage + "Study site \"" + subject.getSite() + "\" does not exist.");
            } else {
                List<String> sitenames = new ArrayList<>();
                for (SiteDefinition sd : sites) {
                    sitenames.add(sd.getSiteOID()); // User needs to put UniqueProtocol ID in, not name
                }
                if (!sitenames.contains(subject.getSite())) {
                    error = new ValidationErrorMessage(commonMessage + "Study site \"" + subject.getSite() + "\" does not exist.");
                }
            }
        }

        if(error != null) {
            error.addOffendingValue("Site: " + subject.getSite());
        }

        return error;
    }

}
