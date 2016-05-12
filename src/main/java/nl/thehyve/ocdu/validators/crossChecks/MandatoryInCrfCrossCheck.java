package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemGroupDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.MandatoryItemInCrfMissing;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class MandatoryInCrfCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        List<String> crfsUsed = data.stream().map(ClinicalData::getCrfName).collect(Collectors.toList());
        Set<String> expectedItemNames = expectedItemNames(metaData, crfsUsed);
        Set<String> presentItemNames = data.stream().map(ClinicalData::getItem).collect(Collectors.toSet());
        if (presentItemNames.containsAll(expectedItemNames)) {
            return null;
        } else {
            MandatoryItemInCrfMissing error = new MandatoryItemInCrfMissing();
            expectedItemNames.stream()
                    .filter(expectedName ->!presentItemNames.contains(expectedName))
                    .forEach(missing -> error.addOffendingValue(missing));
            return error;
        }
    }

    private Set<String> expectedItemNames(MetaData metaData, Collection<String> crfsUsed) {
        List<CRFDefinition> allCRFDefinitions = getAllCRFDefinitions(metaData);
        Set<String> expected = new HashSet<>();
        allCRFDefinitions.stream().filter(crf -> crfsUsed.contains(crf.getName()))
                .forEach(crf -> {
                    List<ItemGroupDefinition> itemGroups = crf.getItemGroups();
                    itemGroups.stream()
                            .forEach(itemGroupDefinition -> itemGroupDefinition.getItems().stream()
                                    .forEach(itemDefinition -> expected.add(itemDefinition.getName())));
                });
        return expected;
    }
}
