package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public interface ClinicalDataCrossCheck {

    ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData,
                                                 Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType>                                                         studySubjectWithEventsTypeList, Map<ClinicalData,
                                                        Boolean> shownMap,
                                                 Map<String, Set<CRFDefinition>> eventMap);
    // All the maps are present here for performance reasons - precomputing all the maps before running checks is much
    // more efficient.

    default boolean isDate(String input) {
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        format.setLenient(false);
        try {
            Date date = format.parse(input);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    default boolean isPDate(String input) {
        DateFormat format1 = new SimpleDateFormat("DD-MMM-YYYY", Locale.ENGLISH);
        format1.setLenient(false);
        DateFormat format2 = new SimpleDateFormat("MMM-YYYY", Locale.ENGLISH);
        format2.setLenient(false);
        DateFormat format3 = new SimpleDateFormat("YYYY", Locale.ENGLISH);
        format3.setLenient(false);
        boolean format1correct = true;
        boolean format2correct = true;
        boolean format3correct = true;
        try {
            Date date = format1.parse(input); //TODO: turn checking format correctness into a function and DRY
        } catch (ParseException e) {
            format1correct = false;
        }
        try {
            Date date = format2.parse(input);
        } catch (ParseException e) {
            format2correct = false;
        }
        try {
            Date date = format3.parse(input);
        } catch (ParseException e) {
            format3correct = false;
        }
        return format1correct || format2correct || format3correct;
    }

    default boolean isInteger(String input) {
        if (input.contains(".") || input.contains(",")) {
            return false;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    default boolean isFloat(String input) {
        if (!input.contains(".")) {
            return false;
        }
        if (input.contains(",")) {
            return false;
        }
        try {
            Float.parseFloat(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
