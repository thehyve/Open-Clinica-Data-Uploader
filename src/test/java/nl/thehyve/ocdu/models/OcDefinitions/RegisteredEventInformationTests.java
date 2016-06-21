package nl.thehyve.ocdu.models.OcDefinitions;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

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

    private static List<StudySubjectWithEventsType> studySubjectWithEventsTypeList;

    @Test
    public void testEventPresentInOC() {
        Map<String, EventResponseType> eventsRegisteredInOpenClinica =
                RegisteredEventInformation.createEventKeyList(studySubjectWithEventsTypeList);
        assertEquals(true, eventsRegisteredInOpenClinica.containsKey("EVENTFULEV-00006SE_REPEATINGEVENT3"));
        assertEquals(false, eventsRegisteredInOpenClinica.containsKey("EVENTFULEV-00006SE_REPEATINGEVENT4"));
        assertEquals(true, eventsRegisteredInOpenClinica.containsKey("EVENTFULSITEEVS-00001SE_REPEATINGEVENT1"));
        assertEquals(false, eventsRegisteredInOpenClinica.containsKey("EVENTFULSITEEVS-00001SE_REPEATINGEVENT2"));
    }

    @BeforeClass
    public static void setup() throws Exception {
        studySubjectWithEventsTypeList =  TestUtils.createStudySubjectWithEventList();
    }
}
