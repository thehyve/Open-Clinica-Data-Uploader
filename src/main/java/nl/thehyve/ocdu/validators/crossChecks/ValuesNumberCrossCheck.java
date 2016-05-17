package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.TooManyValues;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
public class ValuesNumberCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        Map<ClinicalData, ItemDefinition> dataTypeMap = buildItemDefMap(data, metaData);
        List<ClinicalData> violators = data.stream()
                .filter(clinicalData -> isViolator(clinicalData, dataTypeMap)).collect(Collectors.toList());
        if (violators.size() > 0) {
            TooManyValues error = new TooManyValues();
            violators.forEach(clinicalData -> {
                String msg = "Value: " + clinicalData.getValue() + " in item: "+ clinicalData.getItem();
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
