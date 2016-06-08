package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by piotrzakrzewski on 08/06/16.
 */
public class HiddenValueEmptyCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        Map<ClinicalData, ItemDefinition> itemMap = buildItemDefMap(data, metaData);
        data.stream().forEach(clinicalData -> {
            boolean isEmpty = clinicalData.getValue().equals("");
            ItemDefinition itemDefinition = itemMap.get(clinicalData.getItem());
            if (itemDefinition != null) { // non-existent item is a separate check
                itemDefinition.getDisplayRules();
            }
        });
        return null;
    }
}
