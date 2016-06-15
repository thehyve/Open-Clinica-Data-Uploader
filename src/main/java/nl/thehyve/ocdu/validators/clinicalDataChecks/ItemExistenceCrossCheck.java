package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ItemDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class ItemExistenceCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
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

    private Set<String> getAllItemNames(MetaData metaData) {
        Set<String> allItems = new HashSet<>();
        metaData.getItemGroupDefinitions().stream().forEach(itemGroupDefinition ->
                {
                    List<ItemDefinition> items = itemGroupDefinition.getItems();
                    items.stream().forEach(itemDefinition -> {
                        allItems.add(itemDefinition.getName());
                    });
                }
        );
        return allItems;
    }
}
