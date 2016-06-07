package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ItemDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class ItemExistenceCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        Set<String> allItemNames = getAllItemNames(metaData);
        Set<String> missing = data.stream().filter(clinicalData -> {
            String item = clinicalData.getItem();
            return !allItemNames.contains(item);
        }).map(clinicalData -> clinicalData.getItem()).collect(Collectors.toSet());

        if (missing.size() > 0) {
            ItemDoesNotExist error = new ItemDoesNotExist();
            missing.forEach(itemName -> {
                if (itemName.equals("")) {
                    error.addOffendingValue(" (Empty string)");
                } else {
                    error.addOffendingValue(itemName);
                }
            });
            return error;
        } else {
            return null;
        }
    }
}
