package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.junit.BeforeClass;
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
    private static List<ClinicalData> clinicalDataList;
    private static MetaData metaData;

    @Test
    public void testScheduleEventsEmptyParameters() throws Exception {
        OpenClinicaService openClinicaService = new OpenClinicaService();

        String response = openClinicaService.scheduleEvents("",  "hashhashhash", "http://www.example.com",
                metaData, clinicalDataList, studySubjectWithEventsTypeList, "Eventful", null);
        assertEquals("One of the required parameters is missing (username, password, url)", response);

        response = openClinicaService.scheduleEvents("root",  "", "http://www.example.com",
                metaData, clinicalDataList, studySubjectWithEventsTypeList, "Eventful", null);
        assertEquals("One of the required parameters is missing (username, password, url)", response);

        response = openClinicaService.scheduleEvents("root",  "hashhashhash", "",
                metaData, clinicalDataList, studySubjectWithEventsTypeList, "Eventful", null);
        assertEquals("One of the required parameters is missing (username, password, url)", response);
    }

    @Test
    public void testScheduleEvents() throws Exception {
        OpenClinicaService openClinicaService = new OpenClinicaService();

        String response = openClinicaService.scheduleEvents("root", "33a485cb146e1153c69b588c671ab474f2e5b800", "http://localhost:8080/OpenClinica-ws",
                metaData, clinicalDataList, studySubjectWithEventsTypeList, "Eventful", null);
        assertEquals(null, response);
    }

    @BeforeClass
    public static void setup() throws Exception {
        studySubjectWithEventsTypeList =  TestUtils.createStudySubjectWithEventList();
        OcUser ocUser = new OcUser();
        Date now = GregorianCalendar.getInstance().getTime();
        UploadSession uploadSession = new UploadSession("MyFirstUploadSession", UploadSession.Step.EVENTS, now, ocUser);
        Integer groupRepeat = 1;
        Integer eventRepeat = 3;
        ClinicalData clinicalData =
                new ClinicalData("Eventful", " I_MUSTF_AGE", "EV-00006", "RepeatingEvent", eventRepeat,
                        "MUST-FOR_NON_TTP_STUDY", uploadSession, "0.10 ", groupRepeat, ocUser, "64");
        clinicalDataList = new ArrayList<>();
        clinicalDataList.add(clinicalData);
        metaData = new MetaData();
        EventDefinition eventDefinition = new EventDefinition();
        eventDefinition.setStudyEventOID("SE_BLOOMKOOLMETVETTEJUS");
        eventDefinition.setName("Lekker vet");
        metaData.addEventDefinition(eventDefinition);
    }
}
