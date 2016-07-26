package nl.thehyve.ocdu.validators;

import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoField.*;

/**
 * Common utilities for checking primitive types and dates according to OC specification.
 *
 * Created by bo on 7/1/16.
 */
public class UtilChecks {

    private static final String DATE_SEP = "-";
    private static DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendValue(DAY_OF_MONTH, 2).appendLiteral(DATE_SEP)
            .appendValue(MONTH_OF_YEAR, 2).appendLiteral(DATE_SEP)
            .appendValue(YEAR, 4)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT)
            .withChronology(IsoChronology.INSTANCE);


    private static DateTimeFormatter dayOfMonth = new DateTimeFormatterBuilder()
            .appendValue(DAY_OF_MONTH, 2).appendLiteral(DATE_SEP).toFormatter().withResolverStyle(ResolverStyle.STRICT);

    private static DateTimeFormatter month = new DateTimeFormatterBuilder().parseCaseSensitive()
            .appendPattern("MMM").appendLiteral(DATE_SEP).toFormatter().withResolverStyle(ResolverStyle.STRICT);

    private static DateTimeFormatter partialDateFormatter = new DateTimeFormatterBuilder()
            .parseCaseSensitive().appendOptional(dayOfMonth)
            .appendOptional(month).appendValue(YEAR, 4).toFormatter()
            .withChronology(IsoChronology.INSTANCE);


    public static boolean isDate(String input) {
        try {
            LocalDate date = LocalDate.parse(input, dateTimeFormatter);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPDate(String input) {
        try {
            LocalDate date = LocalDate.parse(input, partialDateFormatter);
        } catch (Exception e) {
            if (isMonthAndYear(input) || isYearOnly(input)) {
                return true;
            } else
                return false;
        }
        return true;
    }

    private static boolean isYearOnly(String input) {
        return input.matches("[0-9]{4}");
    }

    private static boolean isMonthAndYear(String input) {
        return input.matches("[A-Z]{1}[a-z]{2}-[0-9]{4}") && monthMatch(input);
    }

    private static final List<String> MONTHS = Arrays.asList(new String[]{"Jan", "Feb", "Mar", "Apr", "May",
            "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});

    private static boolean monthMatch(String input) {
        String[] split = input.split(DATE_SEP);
        if (split.length < 2) return false;
        if (MONTHS.contains(split[0])) return true;
        else
            return false;
    }


    public static boolean isInteger(String input) {
        if (containsAlphaNumeric(input)) {
            return false;
        }
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
        if (containsAlphaNumeric(input)) {
            return false;
        }
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
    private static boolean containsAlphaNumeric(String input) {
        return input.matches(".*[A-z].*");
    }

}
