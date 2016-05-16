package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.MandatoryItemInCrfMissing;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.*;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class MandatoryInCrfCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        HashMap<String, Set<String>> mandatoryMap = getMandatoryMap(data, metaData);
        HashMap<String, Set<String>> presentMap = getPresentMap(data);
        MandatoryItemInCrfMissing error = new MandatoryItemInCrfMissing();
        for (String crfId : mandatoryMap.keySet()) {
            Set<String> expected = mandatoryMap.get(crfId);
            Set<String> found = presentMap.get(crfId);
            expected.stream().filter(expectedItem -> !found.contains(expectedItem)).forEach(missing -> {
                error.addOffendingValue("CRF: " + crfId + " requires item: " + missing);
            });
        }
        if (error.getOffendingValues().size() > 0)
            return error;
        else return null;
    }

    private HashMap<String, Set<String>> getPresentMap(List<ClinicalData> data) {
        HashMap<String, Set<String>> presentMap = new HashMap<>();
        data.stream().forEach(clinicalData -> {
            String crfId = clinicalData.getCrfName() + clinicalData.getCrfVersion();
            if (!presentMap.containsKey(crfId)) {
                Set<String> presentItems = new HashSet<>();
                presentItems.add(clinicalData.getItem());
                presentMap.put(crfId, presentItems);
            } else {
                Set<String> presentItems = presentMap.get(crfId);
                presentItems.add(clinicalData.getItem());
            }
        });
        return presentMap;
    }

    private HashMap<String, Set<String>> getMandatoryMap(List<ClinicalData> data, MetaData metaData) {
        HashMap<String, Set<String>> mandatoryMap = new HashMap<>();
        data.stream().forEach(clinicalData -> {
            String eventName = clinicalData.getEventName();
            String crfName = clinicalData.getCrfName();
            String crfVersion = clinicalData.getCrfVersion();
            CRFDefinition matching = getMatchingCrf(eventName, crfName, crfVersion, metaData);
            if (matching != null) { // Missing CRF or Event are  separate errors
                Set<String> expected = matching.getMandatoryItemNames();
                mandatoryMap.put(crfName + crfVersion, expected);
            }
        });
        return mandatoryMap;
    }




}
