package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.DisplayRule;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ToggleVarForDisplayRuleAbsent;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;

/**
 * Created by piotrzakrzewski on 15/06/16.
 */
public class HiddenTogglePresent implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        ToggleVarForDisplayRuleAbsent error = new ToggleVarForDisplayRuleAbsent();
        Set<String> errors = new HashSet<>();
        for (ClinicalData clinicalData : itemDefMap.keySet()) {
            List<DisplayRule> displayRules = itemDefMap.get(clinicalData).getDisplayRules();
            for (DisplayRule displayRule : displayRules) {
                boolean exists = itemExists(data, displayRule.getControlItemName());
                if (!exists) {
                    error.addOffendingValue(clinicalData.toOffenderString() + " requires: " + displayRule.getControlItemName());
                }
            }
        }
        error.addAllOffendingValues(errors);
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else return null;
    }

    private boolean itemExists(List<ClinicalData> data, String itemName) {
        return data.stream().anyMatch(clinicalData -> clinicalData.getItem().equals(itemName));
    }
}
