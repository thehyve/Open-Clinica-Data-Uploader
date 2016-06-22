package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.DisplayRule;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.clinicalDataChecks.ClinicalDataCrossCheck;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 22/06/16.
 */
public class ClinicalDataChecksRunner {

    private Collection<ClinicalDataCrossCheck> checks = new ArrayList<>();

    public Collection<ClinicalDataCrossCheck> getChecks() {
        return checks;
    }

    public void setChecks(Collection<ClinicalDataCrossCheck> checks) {
        this.checks = checks;
    }

    private final List<ClinicalData> clinicalData;
    private final MetaData metadata;
    /**
     * The list of the current event and crf status of all the subjects present in a study.
     */
    private final List<StudySubjectWithEventsType> subjectWithEventsTypeList;

    public ClinicalDataChecksRunner(MetaData metadata, List<ClinicalData> clinicalData,
                                    List<StudySubjectWithEventsType> subjectWithEventsTypes) {
        this.clinicalData = clinicalData;
        this.metadata = metadata;
        this.subjectWithEventsTypeList = subjectWithEventsTypes;
    }

    public List<ValidationErrorMessage> getErrors() {
        List<ValidationErrorMessage> errors = new ArrayList<>();
        Map<ClinicalData, ItemDefinition> defMap = buildItemDefMap(clinicalData, metadata);
        Map<ClinicalData, Boolean> showMap = buildShownMap(clinicalData, defMap);
        Map<String, Set<CRFDefinition>> eventMap = buildEventMap(metadata);
        checks.stream().forEach(
                check -> {
                    ValidationErrorMessage error = check.getCorrespondingError(clinicalData, metadata,
                            defMap, subjectWithEventsTypeList, showMap, eventMap);
                    if (error != null) errors.add(error);
                }
        );
        return errors;
    }

    public List<ClinicalData> getClinicalData() {
        return clinicalData;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public List<StudySubjectWithEventsType> getSubjectWithEventsTypeList() {
        return subjectWithEventsTypeList;
    }

    private Map<ClinicalData, ItemDefinition> buildItemDefMap(List<ClinicalData> data, MetaData metaData) {
        Map<ClinicalData, ItemDefinition> itemDefMap = new HashMap<>();
        data.forEach(clinicalData -> {
            ItemDefinition itemDefinition = getMatching(clinicalData, metaData);
            if (itemDefinition != null) {
                itemDefMap.put(clinicalData, itemDefinition);
            }
        });
        return itemDefMap;
    }

    private CRFDefinition getMatchingCrf(String eventName, String CRFName, String CRfVersion, MetaData metaData) {
        Map<String, Set<CRFDefinition>> eventMap = buildEventMap(metaData);
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

    private ItemDefinition getMatching(ClinicalData dataPoint, MetaData metaData) {
        CRFDefinition matchingCrf = getMatchingCrf(dataPoint.getEventName(), dataPoint.getCrfName(), dataPoint.getCrfVersion(), metaData);
        if (matchingCrf == null) {
            return null;
        }
        Set<ItemDefinition> itemDefinitions = matchingCrf.allItems();
        List<ItemDefinition> matchingItems = itemDefinitions.stream()
                .filter(itemDefinition -> itemDefinition.getName().equals(dataPoint.getItem()))
                .collect(Collectors.toList());
        assert matchingItems.size() < 2;
        if (matchingItems.size() == 0) return null;
        else return matchingItems.get(0);
    }

    private Map<String, Set<CRFDefinition>> buildEventMap(MetaData metaData) {
        Map<String, Set<CRFDefinition>> eventMap = new HashMap<>();
        metaData.getEventDefinitions().stream().forEach(eventDefinition ->
                {
                    Set<CRFDefinition> crfNames = eventDefinition.getCrfDefinitions()
                            .stream()
                            .collect(Collectors.toSet());
                    eventMap.put(eventDefinition.getName(), crfNames);
                }
        );
        return eventMap;
    }

    /*
    * shown/hidden status is context dependant - it is valid only in context of other data-points for given patient.
    * This is why hidden/shown is not a field of ClinicalData.
    * */
    private Map<ClinicalData, Boolean> buildShownMap(Collection<ClinicalData> data,
                                                     Map<ClinicalData, ItemDefinition> defMap) {
        Map<ClinicalData, Boolean> shownMap = new HashMap<>();
        data.forEach(clinicalData1 -> {
            ItemDefinition definition = defMap.get(clinicalData1);
            boolean shown = determineShown(clinicalData1, data, definition);
            shownMap.put(clinicalData1, shown);
        });
        return shownMap;
    }

    private boolean determineShown(ClinicalData clinicalData1, Collection<ClinicalData> data, ItemDefinition definition) {
        if (definition == null) {
            return true; // This case is covered by separate checks
        }
        List<DisplayRule> displayRules = definition.getDisplayRules();
        boolean satisfied = true;
        for (DisplayRule displayRule : displayRules)
            satisfied = isDisplayRuleSatisfied(displayRule, data, clinicalData1.getSsid());
        return satisfied;
    }

    private boolean isDisplayRuleSatisfied(DisplayRule displayRule, Collection<ClinicalData> data, String subjectId) {
        String controlItemName = displayRule.getControlItemName();
        String optionValue = displayRule.getOptionValue();// This value has to equal value of the controlItemName for given subject
        for (ClinicalData clinicalData1 : data) {
            boolean controlItem = clinicalData1.getItem().equals(controlItemName)
                    && clinicalData1.getSsid().equals(subjectId);
            if (controlItem) {
                return clinicalData1.getValue().equals(optionValue);
            }
        }
        return true;  // controlItemName does not exist is a separate check - therefore we let it pass here
    }
}
