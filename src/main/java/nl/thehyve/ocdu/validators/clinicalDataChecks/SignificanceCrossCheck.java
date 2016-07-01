package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.TooManySignificantDigits;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.UtilChecks;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by piotrzakrzewski on 16/05/16.
 */
public class SignificanceCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap){
        TooManySignificantDigits error = new TooManySignificantDigits();
        data.forEach(clinicalData -> {
            ItemDefinition definition = itemDefMap.get(clinicalData);
            if (definition != null) {
                addOffendingValues(error, clinicalData, definition);
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }

    private void addOffendingValues(TooManySignificantDigits error, ClinicalData clinicalData, ItemDefinition definition) {
        for (String value : clinicalData.getValues()) {
            int digitsAfterDM = getDigitsAfterDM(value);
            if (digitsAfterDM > definition.getSignificantDigits()) {
                String gRepMsg = clinicalData.getGroupRepeat() != null ? "group repeat: " + clinicalData.getGroupRepeat() : "";
                error.addOffendingValue("Item: " + clinicalData.getItem() + gRepMsg +
                        " value: " + value + " expected number of significant digits: "
                        + definition.getSignificantDigits()+ " for subject: "+ clinicalData.getSsid() ) ;
            }
        }
    }

    private int getDigitsAfterDM(String value) {
        if (!UtilChecks.isFloat(value)) {
            return 0;
        } else {
            String[] split = value.split("\\.");
            if (split.length != 2) {
                return 0;
            } else {
                String afterDM = split[1];
                return afterDM.length();
            }
        }
    }
}
