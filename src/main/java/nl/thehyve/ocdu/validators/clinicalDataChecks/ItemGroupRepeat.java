package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.RepeatInNonrepeatingItem;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by piotrzakrzewski on 15/06/16.
 */
public class ItemGroupRepeat implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        RepeatInNonrepeatingItem error = new RepeatInNonrepeatingItem();
        Set<String> reportedItems = new HashSet<>();
        data.stream().forEach(
                clinicalData -> {
                    boolean repeating = !(clinicalData.getGroupRepeat() == null);
                    // Only allowed way to get null as group repeat number is not to include anything past _ in item
                    // column. This means item is non-repeating.
                    ItemDefinition itemDefinition = itemDefMap.get(clinicalData);
                    boolean expectedToBeRepeating = true;
                    if (itemDefinition != null) { // Missing item is a separate check
                        expectedToBeRepeating = itemDefinition.isRepeating();
                        String reportedItem = clinicalData.getItem() + clinicalData.getGroupRepeat();
                        if (repeating && !expectedToBeRepeating && !reportedItems.contains(reportedItem)) {
                            error.addOffendingValue("Item: " + clinicalData.getItem() +
                                    " does not belong to a repeating group, while its repeat literal in the submission " +
                                    "file is: " + clinicalData.getGroupRepeat());
                            reportedItems.add(reportedItem);
                        } else if (!repeating && expectedToBeRepeating && !reportedItems.contains(reportedItem)) {
                            error.addOffendingValue("Item: " + clinicalData.getItem() +
                                    " belongs to a repeating group, while it does not have a repeat specified");
                            reportedItems.add(reportedItem);
                        }
                    }
                }
        );
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }
}
