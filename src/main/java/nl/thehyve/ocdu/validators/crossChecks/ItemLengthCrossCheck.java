package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.FieldLengthExceeded;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class ItemLengthCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        Map<String, Integer> lengthMap = buildFieldLengthMap(metaData);
        FieldLengthExceeded error = new FieldLengthExceeded();
        data.stream().forEach(clinicalData -> {
            String value = clinicalData.getValue();
            String itemName = clinicalData.getItem();
            if (lengthMap.get(itemName) != null && value.length() > lengthMap.get(itemName)) {
                if (lengthMap.get(itemName) != 0) // Length does not have to be defined, in this case it is 0
                    error.addOffendingValue("Item: " + itemName + " value: " + value);
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else return null;
    }
}
