package nl.thehyve.ocdu.validators;


import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.ProtocolFieldRequirementSetting;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EventDataOcChecks {

    public static final int LOCATION_STRING_MAX_LENGTH = 4000;
    public static final DateFormat SIMPLE_DATE_FORMAT;
    public static final DateFormat SIMPLE_TIME_FORMAT;

    static {
        SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SIMPLE_DATE_FORMAT.setLenient(false);

        SIMPLE_TIME_FORMAT = new SimpleDateFormat("H:mm", Locale.ENGLISH);
        SIMPLE_TIME_FORMAT.setLenient(false);
    }

    private final List<Event> events;
    private final MetaData metadata;

    private final Set<String> eventNames;
    private final Set<String> siteNames;

    public EventDataOcChecks(MetaData metadata, List<Event> eventList) {
        this.metadata = metadata;
        this.events = eventList;


        if (metadata.getEventDefinitions() == null) {
            this.eventNames = Collections.emptySet();
        } else {
            this.eventNames =
                    metadata.getEventDefinitions().stream()
                            .map(EventDefinition::getName)
                            .collect(Collectors.toSet());
        }

        if (metadata.getSiteDefinitions() == null) {
            this.siteNames = Collections.emptySet();
        } else {
            this.siteNames = metadata
                    .getSiteDefinitions()
                    .stream()
                    .map(SiteDefinition::getName)
                    .collect(Collectors.toSet());
        }
    }

    public List<ValidationErrorMessage> getErrors() {
        List<ValidationErrorMessage> errors = new ArrayList<>();

        Map<List<String>, Event> keyToEventMap = new HashMap<>();
        for (Event event : events) {
            errors.addAll(validate(event));

            ArrayList<String> key = new ArrayList<>();
            key.add(event.getSsid());
            key.add(event.getEventName());
            if (keyToEventMap.containsKey(key)) {
                ValidationErrorMessage duplicatedEvent = new ValidationErrorMessage(
                        "An event for the given subject is duplicated.");
                duplicatedEvent.addAllOffendingValues(key);
                errors.add(duplicatedEvent);
            } else {
                keyToEventMap.put(key, event);
            }
        }

        return errors;
    }

    protected List<ValidationErrorMessage> validate(Event event) {
        ArrayList<ValidationErrorMessage> errors = new ArrayList<>();

        if (StringUtils.isBlank(event.getSsid())) {
            ValidationErrorMessage subjectIdRequired = new ValidationErrorMessage("Subject id has to be specified.");
            errors.add(subjectIdRequired);
        }

        if (StringUtils.isBlank(event.getEventName())) {
            ValidationErrorMessage eventNameRequired = new ValidationErrorMessage("Event name has to be specified.");
            errors.add(eventNameRequired);
        }

        if (StringUtils.isBlank(event.getStudy())) {
            ValidationErrorMessage studyNameRequired = new ValidationErrorMessage("Study name has to be specified.");
            errors.add(studyNameRequired);
        }

        if (StringUtils.isBlank(event.getStartDate())) {
            ValidationErrorMessage startDateRequired = new ValidationErrorMessage("Start date has to be specified.");
            errors.add(startDateRequired);
        }

        if (StringUtils.isBlank(event.getRepeatNumber())) {
            ValidationErrorMessage repeatNumberRequired = new ValidationErrorMessage(
                    "Repeat number has to be specified.");
            errors.add(repeatNumberRequired);
        }

        if (StringUtils.isNotBlank(event.getStudy()) && !event.getStudy().equals(metadata.getStudyName())) {
            ValidationErrorMessage noSuchStudy =
                    new ValidationErrorMessage("Study name in your event registration file does not match study name " +
                            "in your data file. Expected:" + metadata.getStudyName());
            noSuchStudy.addOffendingValue(event.getStudy());
            errors.add(noSuchStudy);
        }

        if (StringUtils.isNotBlank(event.getEventName()) && !eventNames.contains(event.getEventName())) {
            ValidationErrorMessage noSuchStudy = new ValidationErrorMessage("Event does not exist.");
            noSuchStudy.addOffendingValue(event.getEventName());
            errors.add(noSuchStudy);
        }

        if (StringUtils.isNotBlank(event.getSite()) && !siteNames.contains(event.getSite())) {
            ValidationErrorMessage noSuchSite = new ValidationErrorMessage("Site does not exist.");
            noSuchSite.addOffendingValue(event.getSite());
            errors.add(noSuchSite);
        }

        if (StringUtils.isNotBlank(event.getLocation()) && event.getLocation().length() > LOCATION_STRING_MAX_LENGTH) {
            ValidationErrorMessage locationTooLong = new ValidationErrorMessage("Location name is to long. " +
                    "It has not to exceed " + LOCATION_STRING_MAX_LENGTH + " character in length.");
            locationTooLong.addOffendingValue(event.getLocation());
            errors.add(locationTooLong);
        }

        if (StringUtils.isNotBlank(event.getLocation()) &&
                metadata.getLocationRequirementSetting().equals(ProtocolFieldRequirementSetting.BANNED)) {
            ValidationErrorMessage locationBannedButPresent = new ValidationErrorMessage("Location is not " +
                    "allowed in this study, remove the column or leave fields empty");
            errors.add(locationBannedButPresent);
        }
        if (StringUtils.isBlank(event.getLocation()) &&
                metadata.getLocationRequirementSetting().equals(ProtocolFieldRequirementSetting.MANDATORY)) {
            ValidationErrorMessage locationRequiredButAbsent = new ValidationErrorMessage("Location is " +
                    "required in this study, but one or more of events in your file lack it");
            errors.add(locationRequiredButAbsent);
        }

        Optional<Date> startDateOpt = Optional.empty();
        if (StringUtils.isNotBlank(event.getStartDate())) {
            startDateOpt = parseDateOpt(event.getStartDate());
            if (!startDateOpt.isPresent()) {
                ValidationErrorMessage dateInvalid = new ValidationErrorMessage("Start date is invalid. The date format should be dd-mm-yyyy. For example, 12-10-2014.");
                dateInvalid.addOffendingValue(event.getStartDate());
                errors.add(dateInvalid);
            }
        }

        Optional<Date> startTimeOpt = Optional.empty();
        if (StringUtils.isNotBlank(event.getStartTime())) {
            startTimeOpt = parseTimeOpt(event.getStartTime());
            if (!startTimeOpt.isPresent()) {
                ValidationErrorMessage timeInvalid = new ValidationErrorMessage("Start time is invalid. The time format should be hh:mm. For example, 13:29.");
                timeInvalid.addOffendingValue(event.getStartTime());
                errors.add(timeInvalid);
            }
        }

        Optional<Date> endDateOpt = Optional.empty();
        if (StringUtils.isNotBlank(event.getEndDate())) {
            endDateOpt = parseDateOpt(event.getEndDate());
            if (!endDateOpt.isPresent()) {
                ValidationErrorMessage dateInvalid = new ValidationErrorMessage("End date is invalid. The date format should be dd-mm-yyyy. For example, 12-10-2014.");
                dateInvalid.addOffendingValue(event.getEndDate());
                errors.add(dateInvalid);
            }
        }

        Optional<Date> endTimeOpt = Optional.empty();
        if (StringUtils.isNotBlank(event.getEndTime())) {
            endTimeOpt = parseTimeOpt(event.getEndTime());
            if (!endTimeOpt.isPresent()) {
                ValidationErrorMessage timeInvalid = new ValidationErrorMessage("End time is invalid. The time format should be hh:mm. For example, 13:29.");
                timeInvalid.addOffendingValue(event.getEndTime());
                errors.add(timeInvalid);
            }
        }

        if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
            Date startDate = startDateOpt.get();
            Date endDate = endDateOpt.get();
            if (endDate.before(startDate)) {
                ValidationErrorMessage dateRangeInvalid = new ValidationErrorMessage("Date range is invalid.");
                dateRangeInvalid.addOffendingValue(event.getStartDate());
                dateRangeInvalid.addOffendingValue(event.getEndDate());
                errors.add(dateRangeInvalid);
            } else if (startDate.equals(endDate) && startTimeOpt.isPresent() && endTimeOpt.isPresent()) {
                Date startTime = startTimeOpt.get();
                Date endTime = endTimeOpt.get();
                if (endTime.before(startTime)) {
                    ValidationErrorMessage timeRangeInvalid = new ValidationErrorMessage("Time range is invalid.");
                    timeRangeInvalid.addOffendingValue(event.getStartTime());
                    timeRangeInvalid.addOffendingValue(event.getEndTime());
                    errors.add(timeRangeInvalid);
                }
            }
        }

        if (StringUtils.isNotBlank(event.getRepeatNumber())) {
            Optional<Integer> repeatNumberOpt = parseIntOpt(event.getRepeatNumber());
            if (!repeatNumberOpt.isPresent() || repeatNumberOpt.get() < 1) {
                ValidationErrorMessage noSuchStudy = new ValidationErrorMessage(
                        "Repeat number is not a positive number.");
                noSuchStudy.addOffendingValue(event.getRepeatNumber());
                errors.add(noSuchStudy);
            }
        }

        return errors;
    }

    Optional<Date> parseDateOpt(String dateString) {
        ParsePosition pos = new ParsePosition(0);
        Date time = SIMPLE_DATE_FORMAT.parse(dateString, pos);
        //TODO What if empty space left
        if (pos.getIndex() < dateString.length()) {
            return Optional.empty();
        }
        return Optional.ofNullable(time);
    }

    Optional<Date> parseTimeOpt(String timeString) {
        ParsePosition pos = new ParsePosition(0);
        Date time = SIMPLE_TIME_FORMAT.parse(timeString, pos);
        //TODO What if empty space left
        if (pos.getIndex() < timeString.length()) {
            return Optional.empty();
        }
        return Optional.ofNullable(time);
    }

    Optional<Integer> parseIntOpt(String intString) {
        try {
            return Optional.ofNullable(Integer.valueOf(intString));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
