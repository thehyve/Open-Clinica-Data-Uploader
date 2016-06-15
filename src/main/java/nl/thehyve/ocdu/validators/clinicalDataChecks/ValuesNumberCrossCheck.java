package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.TooManyValues;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
public class ValuesNumberCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        List<ClinicalData> violators = data.stream()
                .filter(clinicalData -> isViolator(clinicalData, itemDefMap) && shownMap.get(clinicalData) ).collect(Collectors.toList());
        if (violators.size() > 0) {
            TooManyValues error = new TooManyValues();
            violators.forEach(clinicalData -> {
                String msg = "Value: " + clinicalData.getValue() + " in item: "+ clinicalData.getItem()
                        + " for subject: "+ clinicalData.getSsid();
                error.addOffendingValue(msg);
            });
            return error;
        } else
            return null;
    }

    private boolean isViolator(ClinicalData dataPoint, Map<ClinicalData, ItemDefinition> itemDefMap) {
        ItemDefinition itemDefinition = itemDefMap.get(dataPoint);
        if (itemDefinition == null) {
            return false; // Missing item is a different error
        }
        boolean multipleValuesAllowed = itemDefinition.isMultiselect();
        boolean hasMultipleValues = dataPoint.getValues().size() > 1;
        if (hasMultipleValues && !multipleValuesAllowed) {
            return true;
        } else {
            return false;
        }
    }
}
