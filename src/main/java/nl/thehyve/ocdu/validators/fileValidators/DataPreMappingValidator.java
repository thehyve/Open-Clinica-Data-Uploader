package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.validators.ClinicalDataChecksRunner;
import nl.thehyve.ocdu.validators.clinicalDataChecks.*;
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
        checks.add(new EventExistsCrossCheck());
        checks.add(new MultipleEventsCrossCheck());
        checks.add(new MultipleCrfCrossCheck());
        this.setChecks(checks);
    }
}
