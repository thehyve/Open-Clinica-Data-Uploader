package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public interface ClinicalDataCrossCheck {

    ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData);

    default Map<String, List<String>> buildEventMap(MetaData metaData) {
        Map<String, List<String>> eventMap = new HashMap<>();
        metaData.getEventDefinitions().stream().forEach(eventDefinition ->
                {
                    List<String> crfNames = eventDefinition.getCrfDefinitions()
                            .stream().map(CRFDefinition::getName)
                            .collect(Collectors.toList());
                    eventMap.put(eventDefinition.getName(), crfNames);
                }
        );
        return eventMap;
    }

    default Map<String, Integer> buildFieldLengthMap(MetaData metaData) {
        Map<String, Integer> lengthMap = new HashMap<>();
        metaData.getItemGroupDefinitions().stream().forEach(itemGroupDefinition ->
                {
                    List<ItemDefinition> items = itemGroupDefinition.getItems();
                    items.stream().forEach(itemDefinition -> {
                        lengthMap.put(itemDefinition.getName(), itemDefinition.getLength());
                    });
                }
        );
        return lengthMap;
    }

    default Set<String> getAllItemNames(MetaData metaData) {
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

    default List<CRFDefinition> getAllCRFDefinitions(MetaData metaData) {
        List<CRFDefinition> allCrfs = new ArrayList<>();
        metaData.getEventDefinitions().stream().forEach(eventDefinition -> {
            allCrfs.addAll(eventDefinition.getCrfDefinitions());
        });
        return allCrfs;
    }

    default List<ItemDefinition> getAllItemDefinitions(MetaData metaData) {
        List<CRFDefinition> allCRFDefinitions = getAllCRFDefinitions(metaData);
        List<ItemDefinition> allItems = new ArrayList<>();
        allCRFDefinitions.stream().forEach(crfDefinition -> {
            crfDefinition.getItemGroups().stream().forEach(itemGroupDefinition -> {

            });
        });
    }
}
