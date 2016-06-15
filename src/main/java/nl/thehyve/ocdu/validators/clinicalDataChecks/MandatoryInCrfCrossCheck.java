package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.MandatoryItemInCrfMissing;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class MandatoryInCrfCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        HashMap<String, Set<String>> mandatoryMap = getMandatoryMap(data, eventMap);
        HashMap<String, Set<String>> presentMap = getPresentMap(data);
        MandatoryItemInCrfMissing error = new MandatoryItemInCrfMissing();
        reportMissingColumns(mandatoryMap, presentMap, error);
        reportMissingValues(mandatoryMap, data, error, shownMap);
        if (error.getOffendingValues().size() > 0)
            return error;
        else return null;
    }

    private void reportMissingValues(HashMap<String, Set<String>> mandatoryMap, List<ClinicalData> data, MandatoryItemInCrfMissing error, Map<ClinicalData, Boolean> shownMap) {
        data.stream().forEach(clinicalData -> {
            String item = clinicalData.getItem();
            String crfId = clinicalData.getCrfName() + clinicalData.getCrfVersion();
            Set<String> mandatory = mandatoryMap.get(crfId);
            String value = clinicalData.getValue();
            if (mandatory != null && mandatory.contains(item) && value.equals("") && shownMap.get(clinicalData)) { // is mandatory and value is empty, item not hidden for given subject
                error.addOffendingValue("Item: " + item + " cannot be empty as it is mandatory in CRF: " + crfId + " but was empty for subject: " + clinicalData.getSsid());
            }
        });
    }


    private void reportMissingColumns(HashMap<String, Set<String>> mandatoryMap, HashMap<String, Set<String>> presentMap, ValidationErrorMessage error) {
        for (String crfId : mandatoryMap.keySet()) {
            Set<String> expected = mandatoryMap.get(crfId);
            Set<String> found = presentMap.get(crfId);
            expected.stream().filter(expectedItem -> !found.contains(expectedItem)).forEach(missing -> {
                error.addOffendingValue("CRF: " + crfId + " requires item: " + missing);
            });
        }
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

    private HashMap<String, Set<String>> getMandatoryMap(List<ClinicalData> data, Map<String, Set<CRFDefinition>> eventMap) {
        HashMap<String, Set<String>> mandatoryMap = new HashMap<>();
        data.stream().forEach(clinicalData -> {
            String eventName = clinicalData.getEventName();
            String crfName = clinicalData.getCrfName();
            String crfVersion = clinicalData.getCrfVersion();
            CRFDefinition matching = getMatchingCrf(eventName, crfName, crfVersion, eventMap);
            if (matching != null) { // Missing CRF or Event are  separate errors
                Set<String> expected = matching.getMandatoryItemNames();
                mandatoryMap.put(crfName + crfVersion, expected);
            }
        });
        return mandatoryMap;
    }

    private CRFDefinition getMatchingCrf(String eventName, String CRFName, String CRfVersion, Map<String, Set<CRFDefinition>> eventMap) {
        Set<CRFDefinition> crfInEvents = eventMap.get(eventName);
        if (crfInEvents == null) {
            return null;
        }
        List<CRFDefinition> matching = crfInEvents.stream()
                .filter(crfDefinition -> crfDefinition.getName().equals(CRFName) && crfDefinition.getVersion().equals(CRfVersion)).collect(Collectors.toList());
        assert matching.size() < 2;
        if (matching.size() == 0) {
            return null;
        } else {
            return matching.get(0);
        }
    }


}
