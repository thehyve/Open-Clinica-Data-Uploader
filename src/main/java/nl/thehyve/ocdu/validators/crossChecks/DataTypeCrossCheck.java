package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.DataTypeMismatch;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.tuple.ImmutablePair;

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

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        Map<ClinicalData, String> itemDataTypes = buildDataTypeMap(data, metaData);
        Set<ImmutablePair<String, String>> offenders = data.stream()
                .filter(clinicalData -> !allValuesMatch(clinicalData.getValues(), itemDataTypes.get(clinicalData)))
                .map(clinicalData -> new ImmutablePair<>(clinicalData.getItem()+" values: "+ clinicalData.getValues(), itemDataTypes.get(clinicalData)))
                .collect(Collectors.toSet());
        if (offenders.size() > 0) {
            DataTypeMismatch error = new DataTypeMismatch();
            offenders.stream().
                    forEach(offender -> error.addOffendingValue("Item: " + offender.left + " expected: " + offender.right));
            return error;
        } else return null;
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
