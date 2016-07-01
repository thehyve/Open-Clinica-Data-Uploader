package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.RangeCheck;
import nl.thehyve.ocdu.models.errors.RangeCheckViolation;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.UtilChecks;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
public class RangeChecks implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        RangeCheckViolation error = new RangeCheckViolation();
        Set<String> alreadyReported = new HashSet<>();
        data.forEach(clinicalData -> {
            ItemDefinition itemDefinition = itemDefMap.get(clinicalData);
            if (itemDefinition != null) { // Nonexistent item is a separate error
                List<RangeCheck> rangeCheckList = itemDefinition.getRangeCheckList();
                rangeCheckList.forEach(rangeCheck -> {
                    List<String> values = clinicalData.getValues();
                    for (String value : values) {
                        if (UtilChecks.isFloat(value) || UtilChecks.isInteger(value)) {
                            BigDecimal intValue = BigDecimal.valueOf(Double.parseDouble(value)); // Do not attempt floating point comparison
                            if (!rangeCheck.isInRange(intValue)) {
                                String gRepMsg = clinicalData.getGroupRepeat() != null ? " group repeat: " + clinicalData.getGroupRepeat() : "";
                                String msg = clinicalData.toOffenderString()+ " " + rangeCheck.violationMessage()
                                        + " but was: " + intValue;
                                if (!alreadyReported.contains(msg)) {
                                    error.addOffendingValue(msg);
                                    alreadyReported.add(msg);
                                }

                            }
                        } // If item is not numeric but should be - there is a separate error for that
                    }
                });
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }
}
