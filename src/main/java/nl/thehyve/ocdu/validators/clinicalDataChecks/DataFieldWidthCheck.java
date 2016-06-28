package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.thehyve.ocdu.validators.fileValidators.GenericFileValidator.MAX_ALLOWED_FIELD_LENGTH;


/**
 * Created by piotrzakrzewski on 22/06/16.
 */
public class DataFieldWidthCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData,
                                                        Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        ValidationErrorMessage error = new ValidationErrorMessage("One or more fields in your " +
                "data violate Open Clinica field width constraints, both item names (columns) and the values need to " +
                "be shorter than " + MAX_ALLOWED_FIELD_LENGTH);
        Set<String> violators = new HashSet<>();
        for (ClinicalData dataPoint : data) {
            if (isTooLong(dataPoint)) {
                String gRepMsg = dataPoint.getGroupRepeat() != null ? " group repeat: " + dataPoint.getGroupRepeat() : "";
                violators.add("Item: " + dataPoint.getItem() + gRepMsg + " of value " + dataPoint.getValue());
            }
        }
        error.addAllOffendingValues(violators);
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else return null;
    }

    private boolean isTooLong(ClinicalData dataPoint) {
        boolean itemLengthViolated = dataPoint.getValue().length() > MAX_ALLOWED_FIELD_LENGTH
                || dataPoint.getItem().length() > MAX_ALLOWED_FIELD_LENGTH;
        return itemLengthViolated;
    }
}
