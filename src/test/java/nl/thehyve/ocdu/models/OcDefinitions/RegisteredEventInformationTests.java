package nl.thehyve.ocdu.models.OcDefinitions;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jacob Rousseau on 20-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class RegisteredEventInformationTests {

    private static final String STUDY_IDENTIFIER = "Eventful";

    private static List<StudySubjectWithEventsType> studySubjectWithEventsTypeList;
    private static List<ClinicalData> clinicalDataList;

    @Test
    public void testEventPresentInOC() {
        Map<String, EventResponseType> eventsRegisteredInOpenClinica =
                RegisteredEventInformation.createEventKeyList(STUDY_IDENTIFIER, null, studySubjectWithEventsTypeList);
        assertEquals(true, eventsRegisteredInOpenClinica.containsKey("EVENTFULEV-00006SE_REPEATINGEVENT3"));
        assertEquals(false, eventsRegisteredInOpenClinica.containsKey("EVENTFULEV-00006SE_REPEATINGEVENT4"));
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
                new ClinicalData(STUDY_IDENTIFIER, "I_MUSTF_AGE", "EV-00006", "RepeatingEvent", eventRepeat,
                        "MUST-FOR_NON_TTP_STUDY", uploadSession, "0.10 ", groupRepeat, ocUser, "64");
        clinicalDataList = new ArrayList<>();
        clinicalDataList.add(clinicalData);
    }
}
