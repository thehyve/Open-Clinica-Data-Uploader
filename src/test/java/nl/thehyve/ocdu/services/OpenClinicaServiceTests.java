package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jacob Rousseau on 20-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class OpenClinicaServiceTests {

    private static List<StudySubjectWithEventsType> studySubjectWithEventsTypeList;
    private static List<Event> eventList;
    private static MetaData metaData;

    @Test
    public void testScheduleEventsEmptyParameters() throws Exception {
        OpenClinicaService openClinicaService = new OpenClinicaService();

        Collection<ValidationErrorMessage> response = openClinicaService.scheduleEvents("",  "hashhashhash", "http://www.example.com",
                metaData, eventList, studySubjectWithEventsTypeList);

        assertTrue(response.contains(new ValidationErrorMessage("One of the required parameters is missing (username, password, url)")));

        response = openClinicaService.scheduleEvents("root",  "", "http://www.example.com",
                metaData, eventList, studySubjectWithEventsTypeList);
        assertTrue(response.contains(new ValidationErrorMessage("One of the required parameters is missing (username, password, url)")));

        response = openClinicaService.scheduleEvents("root",  "hashhashhash", "",
                metaData, eventList, studySubjectWithEventsTypeList);
        assertTrue(response.contains(new ValidationErrorMessage("One of the required parameters is missing (username, password, url)")));
    }

    @Test
    @Ignore("Ignored for now, developed on a personal OpenClinica instance")
    public void testScheduleEvents() throws Exception {
        OpenClinicaService openClinicaService = new OpenClinicaService();

        Collection<ValidationErrorMessage> response = openClinicaService.scheduleEvents("rootsite", "XXXXXXXXX", "http://localhost:8080/OpenClinica-ws",
                metaData, eventList, studySubjectWithEventsTypeList);
        assertTrue(response.isEmpty());
    }

    @BeforeClass
    public static void setup() throws Exception {
        studySubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();

        Integer eventRepeat = 4;

        Event event = new Event();
        event.setStudy("Eventful");
        event.setSite("EventfulSite");
        event.setEventName("RepeatingEvent");
        event.setSsid("EVS-00001");
        event.setLocation("Location");
        event.setRepeatNumber(eventRepeat.toString());

        eventList = new ArrayList<>();
        eventList.add(event);
        metaData = new MetaData();
        EventDefinition eventDefinition = new EventDefinition();
        eventDefinition.setStudyEventOID("SE_REPEATINGEVENT");
        eventDefinition.setName("RepeatingEvent");
        metaData.addEventDefinition(eventDefinition);
    }
}
