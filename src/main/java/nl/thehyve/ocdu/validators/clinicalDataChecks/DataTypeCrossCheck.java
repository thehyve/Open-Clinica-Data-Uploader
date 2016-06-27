package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.DataTypeMismatch;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 12/05/16.
 */
public class DataTypeCrossCheck implements ClinicalDataCrossCheck {
    public final static String TEXT_DATA_TYPE = "text";
    public final static String INTEGER_DATA_TYPE = "integer";
    public final static String FLOAT_DATA_TYPE = "float";
    public final static String DATE_DATA_TYPE = "date";
    public final static String PARTIAL_DATE_DATA_TYPE = "partialDate";
    private static Map<String, String> humanReadbleTypes = initHumanReadbleTypes();

    private static Map<String, String> initHumanReadbleTypes() {
        Map<String, String>  humanReadble = new HashMap<>();
        humanReadble.put(TEXT_DATA_TYPE, "Text" );
        humanReadble.put(INTEGER_DATA_TYPE, "Integer Number (e.g. 2)" );
        humanReadble.put(FLOAT_DATA_TYPE, "Real Number (e.g. 12.3)" );
        humanReadble.put(PARTIAL_DATE_DATA_TYPE, "Partial date (e.g: 1996)" );
        humanReadble.put(DATE_DATA_TYPE, "Full date (e.g: 1988-05-16)" );
        return humanReadble;
    }

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        Map<ClinicalData, String> itemDataTypes = buildDataTypeMap(data, itemDefMap);
        Set<ImmutablePair<String, String>> offenders = data.stream()
                .filter(clinicalData -> !allValuesMatch(clinicalData.getValues(), itemDataTypes.get(clinicalData)) && shownMap.get(clinicalData))
                .map(clinicalData -> new ImmutablePair<>(clinicalData.getItem() + " values: " + clinicalData.getValues(), itemDataTypes.get(clinicalData)))
                .collect(Collectors.toSet());
        if (offenders.size() > 0) {
            DataTypeMismatch error = new DataTypeMismatch();
            offenders.stream().
                    forEach(offender -> {
                        String typeMsg = humanReadbleTypes.get(offender.right);
                        error.addOffendingValue("Item: " + offender.left + " expected: " + typeMsg);
                    });
            return error;
        } else return null;
    }

    private Map<ClinicalData, String> buildDataTypeMap(List<ClinicalData> data, Map<ClinicalData, ItemDefinition> defMap) {
        Map<ClinicalData, String> typeMap = new HashMap<>();
        for (ClinicalData clinicalData : data) {
            ItemDefinition def = defMap.get(clinicalData);
            if (def != null) {
                typeMap.put(clinicalData, def.getDataType());
            }
        }
        return typeMap;
    }

    private boolean allValuesMatch(List<String> values, String expectedType) {
        for (String value : values) {
            if (!matchType(value, expectedType)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchType(String value, String expectedType) {
        if (expectedType == null || expectedType.equals(TEXT_DATA_TYPE)) {
            return true;
        } else if (expectedType.equals(INTEGER_DATA_TYPE)) {
            return isInteger(value);
        } else if (expectedType.equals(FLOAT_DATA_TYPE)) {
            return isFloat(value);
        } else if (expectedType.equals(DATE_DATA_TYPE)) {
            return isDate(value);
        } else if (expectedType.equals(PARTIAL_DATE_DATA_TYPE)) {
            return isPDate(value);
        } else {
            return true; // no expectations, no disappointment
        }
    }

}
