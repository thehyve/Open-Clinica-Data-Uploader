package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.validators.clinicalDataChecks.*;
import nl.thehyve.ocdu.validators.clinicalDataChecks.CRFVersionMatchCrossCheck;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class ClinicalDataOcChecks extends ClinicalDataChecksRunner{

    public ClinicalDataOcChecks(MetaData metadata, List<ClinicalData> clinicalData, List<StudySubjectWithEventsType> subjectWithEventsTypes) {
        super(metadata, clinicalData, subjectWithEventsTypes);
        Collection<ClinicalDataCrossCheck> crossChecks = new ArrayList<>();

        crossChecks.add(new EventExistsCrossCheck());
        crossChecks.add(new DataFieldWidthCrossCheck());
        crossChecks.add(new CrfExistsCrossCheck());
        crossChecks.add(new CRFVersionMatchCrossCheck());
        crossChecks.add(new CrfCouldNotBeVerifiedCrossCheck());
        crossChecks.add(new MultipleEventsCrossCheck());
        crossChecks.add(new MultipleStudiesCrossCheck());
        crossChecks.add(new ItemLengthCrossCheck());
        crossChecks.add(new ItemExistenceCrossCheck());
        // TODO reactive the MandatoryInCrfCrossCheck once the OC-webservice returns the mandatory
        // TODO status of all the CRF-version.
//        crossChecks.add(new MandatoryInCrfCrossCheck());
        crossChecks.add(new DataTypeCrossCheck());
        crossChecks.add(new ValuesNumberCrossCheck());
        crossChecks.add(new RangeChecks());
        crossChecks.add(new SignificanceCrossCheck());
        crossChecks.add(new SsidUniqueCrossCheck());
        crossChecks.add(new EventRepeatCrossCheck());
        crossChecks.add(new CodeListCrossCheck());
        crossChecks.add(new HiddenValueEmptyCheck());
        crossChecks.add(new HiddenTogglePresent());
        crossChecks.add(new ItemGroupRepeat());
        crossChecks.add(new DataFieldWidthCheck());
        crossChecks.add(new EventStatusCheck());
        crossChecks.add(new EventGapCrossCheck());

        this.setChecks(crossChecks);
    }




}
