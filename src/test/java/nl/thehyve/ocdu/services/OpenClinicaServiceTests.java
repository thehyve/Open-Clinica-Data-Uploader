package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

        String response = openClinicaService.scheduleEvents("",  "hashhashhash", "http://www.example.com",
                metaData, eventList, studySubjectWithEventsTypeList);
        assertEquals("One of the required parameters is missing (username, password, url)", response);

        response = openClinicaService.scheduleEvents("root",  "", "http://www.example.com",
                metaData, eventList, studySubjectWithEventsTypeList);
        assertEquals("One of the required parameters is missing (username, password, url)", response);

        response = openClinicaService.scheduleEvents("root",  "hashhashhash", "",
                metaData, eventList, studySubjectWithEventsTypeList);
        assertEquals("One of the required parameters is missing (username, password, url)", response);
    }

    @Test
    @Ignore("Ignored for now, developed on a personal OpenClinica instance")
    public void testScheduleEvents() throws Exception {
        OpenClinicaService openClinicaService = new OpenClinicaService();

        String response = openClinicaService.scheduleEvents("rootsite", "XXXXXXXXX", "http://localhost:8080/OpenClinica-ws",
                metaData, eventList, studySubjectWithEventsTypeList);
        assertEquals("", response);
    }

    @BeforeClass
    public static void setup() throws Exception {
        studySubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();
        OcUser ocUser = new OcUser();
        Date now = GregorianCalendar.getInstance().getTime();
        UploadSession uploadSession = new UploadSession("MyFirstUploadSession", UploadSession.Step.EVENTS, now, ocUser);
        Integer groupRepeat = 1;
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
