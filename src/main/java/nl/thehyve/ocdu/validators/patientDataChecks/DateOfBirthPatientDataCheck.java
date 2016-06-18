package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

/**
 * Created by bo on 6/15/16.
 */
public class DateOfBirthPatientDataCheck implements PatientDataCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(int index, Subject subject, MetaData metaData) {

        int DOBrequired = metaData.getBirthdateRequired();
        for (int i = 0; i < metaData.getSiteDefinitions().size(); i++) {
            SiteDefinition sd = metaData.getSiteDefinitions().get(i);
            int site_dob = sd.getBirthdateRequired();
            //if site requirement for dateOfBirth is more specific than that of study,
            //update DOBrequired
            if (site_dob < DOBrequired) {
                DOBrequired = site_dob;
            }
        }

        String ssid = subject.getSsid();
        String commonMessage = getCommonErrorMessage(index, ssid);

        ValidationErrorMessage error = null;
        String dob = subject.getDateOfBirth();
        if (!StringUtils.isBlank(dob) || DOBrequired < 3) {
            String msg = null;
            if (DOBrequired == 1) {//FULL DATE
                msg = checkFullDate(dob);
            } else if(DOBrequired == 2) { // YEAR ONLY
                msg = checkYearOnly(dob);
            }
            else { // DOBrequired == 3, but the string of dob is not empty
                if(dob.length() == 4) {
                    msg = checkYearOnly(dob);
                }
                else{
                    msg = checkFullDate(dob);
                }
            }

            if(msg != null) {
                error = new ValidationErrorMessage(commonMessage + msg);
            }
        }

        return error;
    }

    private String checkYearOnly(String dob) {
        try {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int birthYear = Integer.valueOf(dob);
            if (birthYear > currentYear) {
                return "Birth year format is invalid.";
            }
            return null;
        } catch (NumberFormatException e) {
            return "Birth year format is invalid.";
        }
    }

    private String checkFullDate(String dob) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        dateFormat.setLenient(false);

        try {
            Date date = dateFormat.parse(dob);
            Date currentDate = new Date();
            if (currentDate.before(date)) {
                return "Birth date should be in the past.";
            }
            return null;
        } catch (ParseException e) {
            return "Birth date format is invalid.";
        }
    }
}
