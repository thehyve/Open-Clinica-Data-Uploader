package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.RangeCheck;
import nl.thehyve.ocdu.models.errors.RangeCheckViolation;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
public class RangeChecks implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        HashMap<String, ItemDefinition> itemMap = getItemMap(metaData);
        RangeCheckViolation error = new RangeCheckViolation();

        data.forEach(clinicalData -> {
            ItemDefinition itemDefinition = itemMap.get(clinicalData.getItem());
            if (itemDefinition != null) { // Nonexistent item is a separate error
                List<RangeCheck> rangeCheckList = itemDefinition.getRangeCheckList();
                rangeCheckList.forEach(rangeCheck -> {
                    String value = clinicalData.getValue();
                    if (isFloat(value) || isInteger(value)) {
                        int intValue = (int) Double.parseDouble(value); // Do not attempt floating point comparison
                        if (!rangeCheck.isInRange(intValue)) {
                            String msg = clinicalData.getItem() + " " + rangeCheck.violationMessage();
                            error.addOffendingValue(msg);
                        }
                    } // If item is not numeric but should be - there is a separate error for that

                });
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }
}
