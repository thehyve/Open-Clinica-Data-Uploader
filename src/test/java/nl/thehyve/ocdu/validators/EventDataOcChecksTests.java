package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.ProtocolFieldRequirementSetting;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

public class EventDataOcChecksTests {

    MetaData metadata;
    Event event;
    List<Event> noEvents = Collections.emptyList();

    @Test
    public void testSuccess() {
        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, empty());
    }

    @Test
    public void testSuccessWithMinimumRequiredFields() {
        event.setSite(null);
        event.setLocation(null);
        event.setStartTime(null);
        event.setEndDate(null);
        event.setEndTime(null);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, empty());
    }

    @Test
    public void testSubjectIdIsRequired() {
        event.setSsid(" ");

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(
                hasProperty("message", is("Subject id has to be specified."))
        ));
    }

    @Test
    public void testEventNameIdIsRequired() {
        event.setEventName(" ");

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(
                hasProperty("message", is("Event name has to be specified."))
        ));
    }

    @Test
    public void testStudyNameIsRequired() {
        event.setStudy(" ");

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(
                hasProperty("message", is("Study name has to be specified."))
        ));
    }

    @Test
    public void testStartDateIsRequired() {
        event.setStartDate(" ");

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(
                hasProperty("message", is("Start date has to be specified."))
        ));
    }

    @Test
    public void testRepeatNumberIsRequired() {
        event.setRepeatNumber(" ");

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(
                hasProperty("message", is("Repeat number has to be specified."))
        ));
    }

    @Test
    public void testEventHasToExist() {
        String eventName = "Un-existing Event";
        event.setEventName(eventName);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Event does not exist.")),
                hasProperty("offendingValues", contains(eventName))
        )));
    }

    @Test
    public void testStudyHasToExist() {
        String studyName = "Un-existing Study";
        event.setStudy(studyName);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is(event.getSsid() + " Study name in your event registration file does not match study name "
                        + "in your data file. Expected:" + metadata.getStudyName())),
                hasProperty("offendingValues", contains(studyName))
        )));
    }

    @Test
    public void testSiteHasToExist() {
        String siteName = "Un-existing Site";
        event.setSite(siteName);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Site does not exist.")),
                hasProperty("offendingValues", contains(siteName))
        )));
    }

    @Test
    public void testLocationStringLength() {
        String siteName = "Un-existing Site";
        int maxLocationNameLength = 4000;
        int violationLocationNameLength = maxLocationNameLength + 1;
        String tooLongLocationName = String.format("%1$" + violationLocationNameLength + "s", "L");
        event.setLocation(tooLongLocationName);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Location name is to long. " +
                        "It has not to exceed " + maxLocationNameLength + " character in length.")),
                hasProperty("offendingValues", contains(tooLongLocationName))
        )));
    }

    @Test
    public void testWrongStartDateFormat() {
        String wrongStartDate = "2004-02-21";
        event.setStartDate(wrongStartDate);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Start date is invalid. The date format should be dd-mm-yyyy. For example, 12-10-2014.")),
                hasProperty("offendingValues", contains(wrongStartDate))
        )));
    }

    @Test
    public void testInvalidStartDate() {
        String invalidStartDate = "31-02-2013";
        event.setStartDate(invalidStartDate);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Start date is invalid. The date format should be dd-mm-yyyy. For example, 12-10-2014.")),
                hasProperty("offendingValues", contains(invalidStartDate))
        )));
    }

    @Test
    public void testWrongEndDateFormat() {
        String wrongStartDate = "2004-02x-21";
        event.setEndDate(wrongStartDate);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("End date is invalid. The date format should be dd-mm-yyyy. For example, 12-10-2014.")),
                hasProperty("offendingValues", contains(wrongStartDate))
        )));
    }

    @Test
    public void testInvalidEndDate() {
        String invalidStartDate = "31-02-2013";
        event.setEndDate(invalidStartDate);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("End date is invalid. The date format should be dd-mm-yyyy. For example, 12-10-2014.")),
                hasProperty("offendingValues", contains(invalidStartDate))
        )));
    }

    @Test
    public void testWrongStartTimeFormat() {
        String wrongStartTime = "12:00AM";
        event.setStartTime(wrongStartTime);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Start time is invalid. The time format should be hh:mm. For example, 13:29.")),
                hasProperty("offendingValues", contains(wrongStartTime))
        )));
    }

    @Test
    public void testInvalidStartTime() {
        String invalidStartDate = "24:00";
        event.setStartTime(invalidStartDate);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Start time is invalid. The time format should be hh:mm. For example, 13:29.")),
                hasProperty("offendingValues", contains(invalidStartDate))
        )));
    }

    @Test
    public void testWrongEndTimeFormat() {
        String wrongEndTime = "12:00AM";
        event.setEndTime(wrongEndTime);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("End time is invalid. The time format should be hh:mm. For example, 13:29.")),
                hasProperty("offendingValues", contains(wrongEndTime))
        )));
    }

    @Test
    public void testInvalidEndTime() {
        String invalidEndDate = "24:00";
        event.setEndTime(invalidEndDate);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("End time is invalid. The time format should be hh:mm. For example, 13:29.")),
                hasProperty("offendingValues", contains(invalidEndDate))
        )));
    }

    @Test
    public void testInvalidDateRange() {
        String startDate = "22-02-2014";
        String endDate = "21-02-2014";
        event.setStartDate(startDate);
        event.setEndDate(endDate);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Date range is invalid.")),
                hasProperty("offendingValues", contains(startDate, endDate))
        )));
    }

    @Test
    public void testInvalidTimeRange() {
        String date = "22-02-2014";
        String startTime = "10:00";
        String endTime = "7:00";
        event.setStartDate(date);
        event.setStartTime(startTime);
        event.setEndDate(date);
        event.setEndTime(endTime);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Time range is invalid.")),
                hasProperty("offendingValues", contains(startTime, endTime))
        )));
    }

    @Test
    public void testRepeatNumberWrongFormat() {
        String repeatNumber = "one";
        event.setRepeatNumber(repeatNumber);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, noEvents);
        List<ValidationErrorMessage> errors = checks.validate(event);

        assertThat(errors, contains(allOf(
                hasProperty("message", is("Repeat number is not a positive number.")),
                hasProperty("offendingValues", contains(repeatNumber))
        )));
    }

    @Test
    public void testDuplicatedEvents() {
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);

        String anotherEventName = event.getEventName() + "(another)";
        EventDefinition anotherEventDefinition = new EventDefinition();
        anotherEventDefinition.setName(anotherEventName);
        metadata.getEventDefinitions().add(anotherEventDefinition);
        Event event1 = new Event();
        event1.setStudy(event.getStudy());
        event1.setEventName(anotherEventName);
        event1.setSsid(event.getSsid());
        event1.setStartDate("10-06-2014");
        event1.setRepeatNumber("2");
        events.add(event1);

        events.add(event1);
        events.add(event1);

        EventDataOcChecks checks = new EventDataOcChecks(metadata, events);
        List<ValidationErrorMessage> errors = checks.getErrors();

        assertThat(errors, contains(
                allOf(
                        hasProperty("message", is("An event for the given subject is duplicated.")),
                        hasProperty("offendingValues", contains(event.getSsid(), anotherEventName, "2"))
                ),
                allOf(
                        hasProperty("message", is("An event for the given subject is duplicated.")),
                        hasProperty("offendingValues", contains(event.getSsid(), anotherEventName, "2"))
                )));
    }

    @Before
    public void setUp() throws Exception {
        String sid = "subj1";
        String studyName = "Test Study";
        String eventName = "Test Event";
        String siteName = "Test Site";

        metadata = new MetaData();
        metadata.setStudyName(studyName);
        ArrayList<EventDefinition> eventDefinitions = new ArrayList<>();
        EventDefinition eventDefinition = new EventDefinition();
        eventDefinition.setName(eventName);
        eventDefinitions.add(eventDefinition);
        metadata.setEventDefinitions(eventDefinitions);
        ArrayList<SiteDefinition> siteDefinitions = new ArrayList<>();
        SiteDefinition siteDefinition = new SiteDefinition();
        siteDefinition.setName(siteName);
        siteDefinitions.add(siteDefinition);
        metadata.setSiteDefinitions(siteDefinitions);

        event = new Event();
        event.setStudy(studyName);
        event.setEventName(eventName);
        event.setSite(siteName);
        event.setSsid(sid);
        event.setStartDate("22-02-2014");
        event.setStartTime("0:00");
        event.setEndDate("23-02-2014");
        event.setEndTime("23:59");
        event.setRepeatNumber("2");
        event.setLocation("Test Location");
    }

    @Test
    public void locationBannedTest() throws Exception {
        metadata.setLocationRequirementSetting(ProtocolFieldRequirementSetting.BANNED);
        List<Event> events = new ArrayList<>();
        events.add(event);
        EventDataOcChecks checks = new EventDataOcChecks(metadata, events);
        List<ValidationErrorMessage> errors = checks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(1));
        assertThat(errors.get(0).getMessage(), containsString("Location is not allowed in this study"));
    }

    @Test
    public void locationRequiredTest() throws Exception {
        metadata.setLocationRequirementSetting(ProtocolFieldRequirementSetting.MANDATORY);
        List<Event> events = new ArrayList<>();
        event.setLocation("");
        events.add(event);
        EventDataOcChecks checks = new EventDataOcChecks(metadata, events);
        List<ValidationErrorMessage> errors = checks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(1));
        assertThat(errors.get(0).getMessage(), containsString("Location is required"));
    }

    @Test
    public void locationOptionalEmptyTest() throws Exception {
        metadata.setLocationRequirementSetting(ProtocolFieldRequirementSetting.OPTIONAL);
        List<Event> events = new ArrayList<>();
        event.setLocation("");
        events.add(event);
        EventDataOcChecks checks = new EventDataOcChecks(metadata, events);
        List<ValidationErrorMessage> errors = checks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(0));
    }

    @Test
    public void locationOptionalFilledTest() throws Exception {
        metadata.setLocationRequirementSetting(ProtocolFieldRequirementSetting.OPTIONAL);
        List<Event> events = new ArrayList<>();
        events.add(event);
        EventDataOcChecks checks = new EventDataOcChecks(metadata, events);
        List<ValidationErrorMessage> errors = checks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(0));
    }

}
