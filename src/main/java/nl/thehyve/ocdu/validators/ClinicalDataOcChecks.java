package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.DisplayRule;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.checks.DataFieldWidthCheck;
import nl.thehyve.ocdu.validators.checks.OcEntityCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.*;
import nl.thehyve.ocdu.validators.clinicalDataChecks.CRFVersionMatchCrossCheck;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class ClinicalDataOcChecks {


    private final List<ClinicalData> clinicalData;
    private final MetaData metadata;

    /**
     * The list of the current event and crf status of all the subjects present in a study.
     */
    private final List<StudySubjectWithEventsType> subjectWithEventsTypeList;

    private List<OcEntityCheck> recordChecks = new ArrayList<>();
    private List<ClinicalDataCrossCheck> crossChecks = new ArrayList<>();

    public ClinicalDataOcChecks(MetaData metadata, List<ClinicalData> clinicalData, List<StudySubjectWithEventsType> subjectWithEventsTypes) {
        this.metadata = metadata;
        this.clinicalData = clinicalData;
        this.subjectWithEventsTypeList = subjectWithEventsTypes;
        recordChecks.add(new DataFieldWidthCheck());

        crossChecks.add(new EventExistsCrossCheck());
        crossChecks.add(new DataFieldWidthCrossCheck());
        crossChecks.add(new CrfExistsCrossCheck());
        crossChecks.add(new CRFVersionMatchCrossCheck());
        crossChecks.add(new CrfCouldNotBeVerifiedCrossCheck());
        crossChecks.add(new MultipleEventsCrossCheck());
        crossChecks.add(new MultipleStudiesCrossCheck());
        crossChecks.add(new ItemLengthCrossCheck());
        crossChecks.add(new ItemExistenceCrossCheck());
        crossChecks.add(new MandatoryInCrfCrossCheck());
        crossChecks.add(new DataTypeCrossCheck());
        crossChecks.add(new ValuesNumberCrossCheck());
        crossChecks.add(new RangeChecks());
        crossChecks.add(new SignificanceCrossCheck());
        crossChecks.add(new SsidUniqueCrossCheck());
        crossChecks.add(new EventRepeatCrossCheck());
        crossChecks.add(new CodeListCrossCheck());
    }

    public List<ValidationErrorMessage> getErrors() {
        List<ValidationErrorMessage> errors = new ArrayList<>();
        clinicalData.stream().forEach(cData ->
        {
            List<ValidationErrorMessage> recordErrors = getRecordErrors(cData);
            errors.addAll(recordErrors);
        });
        List<ValidationErrorMessage> interRecordInconsitencies = getCrossCheckErrors(clinicalData);
        errors.addAll(interRecordInconsitencies);
        return errors;
    }

    private List<ValidationErrorMessage> getRecordErrors(ClinicalData data) {
        List<ValidationErrorMessage> errors = new ArrayList<>();
        recordChecks.stream().forEach(
                check -> {
                    ValidationErrorMessage error = check.getCorrespondingError(data, metadata);
                    if (error != null) errors.add(error);
                }
        );
        return errors;
    }

    private List<ValidationErrorMessage> getCrossCheckErrors(List<ClinicalData> data) {
        List<ValidationErrorMessage> errors = new ArrayList<>();
        Map<ClinicalData, ItemDefinition> defMap = buildItemDefMap(data, metadata);
        Map<ClinicalData, Boolean> showMap = new HashMap<>();
        Map<String, Set<CRFDefinition>> eventMap = buildEventMap(metadata);
        crossChecks.stream().forEach(
                check -> {
                    ValidationErrorMessage error = check.getCorrespondingError(data, metadata,
                            defMap, subjectWithEventsTypeList, showMap, eventMap);
                    if (error != null) errors.add(error);
                }
        );
        return errors;
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
        List<DisplayRule> displayRules = definition.getDisplayRules();
        boolean satisfied = true;
        for (DisplayRule displayRule: displayRules)
            satisfied = isDisplayRuleSatisfied(displayRule, data, clinicalData1.getSsid());
        return satisfied;
    }

    private boolean isDisplayRuleSatisfied(DisplayRule displayRule, Collection<ClinicalData> data, String subjectId) {
        String appliesInCrf = displayRule.getAppliesInCrf();
        String controlItemName = displayRule.getControlItemName();
        String optionValue = displayRule.getOptionValue();// This value has to equal value of the controlItemName for given subject
        for (ClinicalData clinicalData1: data)  {
            boolean controlItem = clinicalData1.getCrfName().equals(appliesInCrf)
                    && clinicalData1.getItem().equals(controlItemName)
                    && clinicalData1.getSsid().equals(subjectId);
            if (controlItem) {
                return clinicalData1.getValue().equals(optionValue);
            }
        }
        return true;  // controlItemName does not exist is a separate check - therefore we let it pass here
    }

}
