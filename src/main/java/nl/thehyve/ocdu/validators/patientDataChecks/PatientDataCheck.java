package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by bo on 6/7/16.
 */
public interface PatientDataCheck {
    ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData);

    default String getCommonErrorMessage(int index, String ssid) {
        return "Line " + index + " (subjectID = " + ssid + ") : ";
    }
}
