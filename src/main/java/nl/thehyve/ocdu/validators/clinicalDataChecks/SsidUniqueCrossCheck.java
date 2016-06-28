package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.SSIDDuplicated;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 16/05/16.
 */
public class SsidUniqueCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        HashMap<String, List<String>> rowMap = new HashMap<>();
        data.stream().forEach(clinicalData -> {
            String rowString = toRowIdString(clinicalData);
            List<String> items;
            if (!rowMap.containsKey(rowString)) {
                items = new ArrayList<>();
                rowMap.put(rowString, items);
            } else {
                items = rowMap.get(rowString);
            }
            items.add(clinicalData.getItem());
        });
        SSIDDuplicated error = new SSIDDuplicated();
        List<String> offenders = getOffenders(rowMap);
        error.addAllOffendingValues(offenders);
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }

    private List<String> getOffenders(HashMap<String, List<String>> rowMap) {
        List<String> offenders = new ArrayList<>();
        for (String key : rowMap.keySet()) {
            List<String> items = rowMap.get(key);
            List<String> uniqueItems = items.stream().distinct().collect(Collectors.toList());
            if (items.size() != uniqueItems.size()) {
                offenders.add(key);
            }
        }
        return offenders;
    }

    private String toRowIdString(ClinicalData clinicalData) {
        String gRepeat = clinicalData.getGroupRepeat() != null ? clinicalData.getGroupRepeat().toString() : "";
        return clinicalData.getSsid() + "in: "
                + clinicalData.getEventName() + " "
                + clinicalData.getEventRepeat() + " "
                + clinicalData.getCrfName() + " "
                + gRepeat + " "
                + clinicalData.getCrfVersion();
    }
}
