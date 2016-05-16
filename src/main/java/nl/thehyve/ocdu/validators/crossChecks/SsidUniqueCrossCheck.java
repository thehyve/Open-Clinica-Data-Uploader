package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.SSIDDuplicated;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 16/05/16.
 */
public class SsidUniqueCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
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
        for (String key: rowMap.keySet()) {
            List<String> items = rowMap.get(key);
            List<String> uniqueItems = items.stream().distinct().collect(Collectors.toList());
            if (items.size() != uniqueItems.size()) {
                offenders.add(key);
            }
        }
        return offenders;
    }

    private String toRowIdString(ClinicalData clinicalData) {
        return clinicalData.getSsid() + "in: "
                + clinicalData.getEventName() + " "
                + clinicalData.getCrfName() + " "
                + clinicalData.getCrfVersion();
    }
}
