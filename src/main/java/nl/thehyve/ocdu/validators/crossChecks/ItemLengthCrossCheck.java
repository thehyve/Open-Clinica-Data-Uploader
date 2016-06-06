package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.FieldLengthExceeded;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Map;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class ItemLengthCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, List<StudySubjectWithEventsType> subjectWithEventsTypeList) {
        Map<ClinicalData, Integer> lengthMap = buildFieldLengthMap(data, metaData);
        FieldLengthExceeded error = new FieldLengthExceeded();
        data.stream().forEach(clinicalData -> {
            String value = clinicalData.getValue();
            String itemName = clinicalData.getItem();
            if (lengthMap.get(clinicalData) != null && value.length() > lengthMap.get(clinicalData)) {
                if (lengthMap.get(clinicalData) != 0) // Length does not have to be defined, in this case it is 0
                    error.addOffendingValue("Item: " + itemName + " value: " + value + " allowed length: "
                            + lengthMap.get(clinicalData)
                            + " for subject: " + clinicalData.getSsid());
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else return null;
    }
}
