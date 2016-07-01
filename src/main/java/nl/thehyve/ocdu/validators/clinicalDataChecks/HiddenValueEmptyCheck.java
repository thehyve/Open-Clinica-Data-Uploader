package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.DisplayRule;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.HiddenValueError;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by piotrzakrzewski on 08/06/16.
 */
public class HiddenValueEmptyCheck implements ClinicalDataCrossCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        HiddenValueError error = new HiddenValueError();
        data.stream().forEach(clinicalData -> {
            boolean nonEmpty = !clinicalData.getValue().equals("");
            boolean isHidden = !shownMap.get(clinicalData);
            if (nonEmpty && isHidden) {
                error.addOffendingValue(clinicalData.toOffenderString() +
                        " is hidden, it is not allowed to provide any value for it. Value provided: " +
                        clinicalData.getValue());
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }
}
