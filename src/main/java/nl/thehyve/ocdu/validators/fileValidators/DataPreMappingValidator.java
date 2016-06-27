package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.validators.ClinicalDataChecksRunner;
import nl.thehyve.ocdu.validators.clinicalDataChecks.ClinicalDataCrossCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.CrfCouldNotBeVerifiedCrossCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.CrfExistsCrossCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.SiteSubjectMatchCrossCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.SitesExistCrossCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.StudyStatusAvailable;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by piotrzakrzewski on 22/06/16.
 */
public class DataPreMappingValidator extends ClinicalDataChecksRunner {

    public DataPreMappingValidator(MetaData metadata, List<ClinicalData> clinicalData, List<StudySubjectWithEventsType> subjectWithEventsTypes) {
        super(metadata, clinicalData, subjectWithEventsTypes);
        Collection<ClinicalDataCrossCheck> checks = new ArrayList<>();
        checks.add(new SitesExistCrossCheck());
        checks.add(new SiteSubjectMatchCrossCheck());
        checks.add(new CrfExistsCrossCheck());
        checks.add(new StudyStatusAvailable());
        checks.add(new CrfCouldNotBeVerifiedCrossCheck());
        this.setChecks(checks);
    }
}
