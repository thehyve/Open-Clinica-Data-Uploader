package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public interface ClinicalDataCrossCheck {

    ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData);

    default Map<String, List<CRFDefinition>> buildEventMap(MetaData metaData) {
        Map<String, List<CRFDefinition>> eventMap = new HashMap<>();
        metaData.getEventDefinitions().stream().forEach(eventDefinition ->
                {
                    List<CRFDefinition> crfNames = eventDefinition.getCrfDefinitions()
                            .stream()
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

    default Set<String> getAllItemNames(List<ClinicalData> data) {
        return null;
    }

    default List<CRFDefinition> getAllCRFDefinitions(MetaData metaData) {
        List<CRFDefinition> allCrfs = new ArrayList<>();
        metaData.getEventDefinitions().stream().forEach(eventDefinition -> {
            allCrfs.addAll(eventDefinition.getCrfDefinitions());
        });
        return allCrfs;
    }

    default Map<String, String> buildDataTypeMap(MetaData metaData) {
        List<CRFDefinition> allCRFDefinitions = getAllCRFDefinitions(metaData);
        Map<String, String> dataTypeMap = new HashMap<>();
        allCRFDefinitions.stream().forEach(crfDefinition -> {
            crfDefinition.allItems().stream().forEach(itemDefinition -> {
                        dataTypeMap.put(itemDefinition.getName(), itemDefinition.getDataType());
                    }
            );
        });


        return dataTypeMap;
    }


    default CRFDefinition getMatching(String eventName, String CRFName, String CRfVersion, Map<String, List<CRFDefinition>> eventMap) {
        List<CRFDefinition> crfInEvents = eventMap.get(eventName);
        if (crfInEvents == null) {
            return null;
        }
        List<CRFDefinition> matching = crfInEvents.stream()
                .filter(crfDefinition -> crfDefinition.getName().equals(CRFName) && crfDefinition.getVersion().equals(CRfVersion)).collect(Collectors.toList());
        if (matching.size() == 0) {
            return null;
        } else {
            return matching.get(0);
        }
    }

    default boolean isDate(String input) {
        DateFormat format = new SimpleDateFormat("YYYY-MM-DD", Locale.ENGLISH);
        try {
            Date date = format.parse(input);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    default boolean isPDate(String input) {
        DateFormat format1 = new SimpleDateFormat("DD-MMM-YYYY", Locale.ENGLISH);
        DateFormat format2 = new SimpleDateFormat("MMM-YYYY", Locale.ENGLISH);
        DateFormat format3 = new SimpleDateFormat("YYYY", Locale.ENGLISH);
        boolean format1correct = true;
        boolean format2correct = true;
        boolean format3correct = true;
        try {
            Date date = format1.parse(input); //TODO: turn checking format correctness into a function and DRY
        } catch (ParseException e) {
            format1correct = false;
        }
        try {
            Date date = format2.parse(input);
        } catch (ParseException e) {
            format2correct = false;
        }
        try {
            Date date = format3.parse(input);
        } catch (ParseException e) {
            format3correct = false;
        }
        return format1correct || format2correct || format3correct;
    }

    default boolean isInteger(String input) {
        if (input.contains(".") || input.contains(",")) {
            return false;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    default boolean isFloat(String input) {
        if (!input.contains(".")) {
            return false;
        }
        if (input.contains(",")) {
            return false;
        }
        try {
            Float.parseFloat(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
