package nl.thehyve.ocdu.models.OcDefinitions;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Jacob Rousseau on 20-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class RegisteredEventInformationTests {

    private static List<StudySubjectWithEventsType> studySubjectWithEventsTypeList;

    private static MetaData metaData;

    @Test
    public void testEventPresentInOC() {
        Map<String, EventResponseType> eventsRegisteredInOpenClinica =
                RegisteredEventInformation.createEventKeyList(studySubjectWithEventsTypeList);
        assertEquals(true, eventsRegisteredInOpenClinica.containsKey("EVENTFULEV-00006SE_REPEATINGEVENT3"));
        assertEquals(false, eventsRegisteredInOpenClinica.containsKey("EVENTFULEV-00006SE_REPEATINGEVENT8"));
        assertEquals(true, eventsRegisteredInOpenClinica.containsKey("EVENTFULEVENTFULSITEEVS-00001SE_REPEATINGEVENT1"));
        assertEquals(false, eventsRegisteredInOpenClinica.containsKey("EVENTFULEVENTFULSITEEVS-00001SE_REPEATINGEVENT5"));
    }

    @Test
    public void testMissingEventPerSubject() {
        Set<ImmutablePair> patInEv = new HashSet<>();
        patInEv.add(new ImmutablePair("EV-00003", "EVENTFUL"));
        Map<String, List<EventDefinition>> missingEventsInOpenClinicaPerSubject =
                RegisteredEventInformation.getMissingEventsPerSubject(metaData, studySubjectWithEventsTypeList, patInEv);
        List<EventDefinition> missingList = missingEventsInOpenClinicaPerSubject.get("EV-00003");
        assertEquals(1, missingList.size());
        assertEquals("SE_EVENTFUL", missingList.get(0).getStudyEventOID());
    }

    @BeforeClass
    public static void setup() throws Exception {
        studySubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();
        metaData = new MetaData();
        EventDefinition eventDefinitionRepeatingEvent = new EventDefinition();
        eventDefinitionRepeatingEvent.setStudyEventOID("SE_REPEATINGEVENT");

        EventDefinition eventDefinitionEvent = new EventDefinition();
        eventDefinitionEvent.setStudyEventOID("SE_EVENTFUL");
        eventDefinitionEvent.setName("EVENTFUL");


        List<EventDefinition> eventDefinitionList = new ArrayList<>();
        eventDefinitionList.add(eventDefinitionRepeatingEvent);
        eventDefinitionList.add(eventDefinitionEvent);


        metaData.setEventDefinitions(eventDefinitionList);
    }
}
