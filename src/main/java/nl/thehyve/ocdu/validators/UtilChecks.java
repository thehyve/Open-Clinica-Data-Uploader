package nl.thehyve.ocdu.validators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bo on 7/1/16.
 */
public class UtilChecks {

    public static boolean isDate(String input) {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        format.setLenient(false);
        try {
            Date date = format.parse(input);
            String[] split = input.split("-");
            Integer day = Integer.parseInt(split[0]);
            Integer month = Integer.parseInt(split[1]);
            if (day > 31 || month > 12) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPDate(String input) {
        if (!isDateString(input)) return false;
        DateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        format1.setLenient(false);
        DateFormat format2 = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
        format2.setLenient(false);
        DateFormat format3 = new SimpleDateFormat("yyyy", Locale.ENGLISH);
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
            if (input.length() != 4) format3correct = false;
        } catch (ParseException e) {
            format3correct = false;
        }
        return format1correct || format2correct || format3correct;
    }

    private static boolean isDateString(String input) {
        String[] split = input.split("-");
        if (split.length > 3) return false; //TODO: look for a lib maybe in Apache Commons to do Date Validation
        if (!input.matches(".*[0-9]{4}$")) return false; // contains 4 digits at the end
        if (input.matches("[0-9]{1,2}-.*")) { // if starts with days, then days cannot exceed 31
            String s = split[0];
            Integer day = Integer.parseInt(s);
            if (day > 31) return false;
        }
        return true;
    }

    public static boolean isInteger(String input) {
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

    public static boolean isFloat(String input) {
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
