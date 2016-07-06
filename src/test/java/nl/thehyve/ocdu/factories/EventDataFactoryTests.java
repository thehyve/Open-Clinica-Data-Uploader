package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EventDataFactoryTests {

    private EventDataFactory factory;
    private UploadSession testSubmission;
    private OcUser testUser;
    private MetaData metadata;

    @Test
    public void testMapRowWithOnlySomeColumns() {
        HashMap<String, Integer> columnsIndes = new HashMap<>();
        columnsIndes.put("Study Subject ID", 0);
        columnsIndes.put("Event Name", 1);
        columnsIndes.put("Study", 2);
        columnsIndes.put("Start Date", 3);
        columnsIndes.put("Repeat Number", 4);
        String[] row = new String[]{"Ssid0", "Event Name", "Study", "11-Jun-2014", "5"};

        Event event = factory.mapRow(row, columnsIndes);

        assertThat(event, allOf(
                hasProperty("owner", equalTo(testUser)),
                hasProperty("submission", equalTo(testSubmission)),
                hasProperty("ssid", equalTo("Ssid0")),
                hasProperty("eventName", equalTo("Event Name")),
                hasProperty("study", equalTo("Study")),
                hasProperty("startDate", equalTo("11-Jun-2014")),
                hasProperty("repeatNumber", equalTo("5"))
        ));
    }

    @Test
    public void testMapRow() {
        HashMap<String, Integer> columnsIndes = new HashMap<>();
        columnsIndes.put("Study Subject ID", 0);
        columnsIndes.put("Event Name", 1);
        columnsIndes.put("Study", 2);
        columnsIndes.put("Site", 3);
        columnsIndes.put("Location", 4);
        columnsIndes.put("Start Date", 5);
        columnsIndes.put("Start Time", 6);
        columnsIndes.put("End Date", 7);
        columnsIndes.put("End Time", 8);
        columnsIndes.put("Repeat Number", 9);
        String[] row = new String[]{"Ssid", "Event Name", "Study", "Site", "Location",
                "21-Feb-2011", "12:00", "22-Feb-2011", "18:00", "3"};

        Event event = factory.mapRow(row, columnsIndes);

        assertThat(event, allOf(
                hasProperty("owner", equalTo(testUser)),
                hasProperty("submission", equalTo(testSubmission)),
                hasProperty("ssid", equalTo("Ssid")),
                hasProperty("eventName", equalTo("Event Name")),
                hasProperty("study", equalTo("Study")),
                hasProperty("site", equalTo("Site")),
                hasProperty("location", equalTo("Location")),
                hasProperty("startDate", equalTo("21-Feb-2011")),
                hasProperty("startTime", equalTo("12:00")),
                hasProperty("endDate", equalTo("22-Feb-2011")),
                hasProperty("endTime", equalTo("18:00")),
                hasProperty("repeatNumber", equalTo("3"))
        ));
    }

    @Test
    public void createEventsData() {
        Path testFilePath = Paths.get("docs/exampleFiles/events.txt");

        List<Event> events = factory.createEventsData(testFilePath);

        assertThat(events, contains(
                allOf(
                        hasProperty("owner", equalTo(testUser)),
                        hasProperty("submission", equalTo(testSubmission)),
                        hasProperty("ssid", equalTo("Subject1")),
                        hasProperty("eventName", equalTo("Event 1")),
                        hasProperty("study", equalTo("Study 1")),
                        hasProperty("site", equalTo("Site 1")),
                        hasProperty("location", equalTo("Location 1")),
                        hasProperty("startDate", equalTo("12-Apr-2013")),
                        hasProperty("startTime", equalTo("10:00")),
                        hasProperty("endDate", equalTo("14-Apr-2013")),
                        hasProperty("endTime", equalTo("14:00")),
                        hasProperty("repeatNumber", equalTo("1"))
                ),
                allOf(
                        hasProperty("owner", equalTo(testUser)),
                        hasProperty("submission", equalTo(testSubmission)),
                        hasProperty("ssid", equalTo("Subject2")),
                        hasProperty("eventName", equalTo("Event 2")),
                        hasProperty("study", equalTo("Study 2")),
                        hasProperty("site", equalTo("Site 2")),
                        hasProperty("location", equalTo("Location 2")),
                        hasProperty("startDate", equalTo("2-Sep-2012")),
                        hasProperty("startTime", equalTo("11:25")),
                        hasProperty("endDate", equalTo("20-Oct-2012")),
                        hasProperty("endTime", equalTo("18:10")),
                        hasProperty("repeatNumber", equalTo("2"))
                )
        ));
    }

    @Test
    public void testGenerateEventSchedulingTemplate() throws Exception{
        List<StudySubjectWithEventsType> studySubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();

        Set<ImmutablePair> patInEv = new HashSet<>();
        patInEv.add(new ImmutablePair("EV-00007", "RepeatingEvent"));
        patInEv.add(new ImmutablePair("EV-00007", "Non-repeating Event"));
        List<String> result = this.factory.generateEventSchedulingTemplate(this.metadata, studySubjectWithEventsTypeList, patInEv);
        assertTrue(result.contains("EV-00007\tRepeatingEvent\tEventful\t\t\t\t\t\n"));
        assertTrue(result.contains("EV-00007\tNon-repeating Event\tEventful\t\t\t\t\t\n"));

    }

    @Before
    public void setUp() throws Exception {
        this.testUser = new OcUser();
        this.testUser.setUsername("tester");
        this.testSubmission = new UploadSession("submission1", UploadSession.Step.MAPPING, new Date(), this.testUser);
        this.factory = new EventDataFactory(testUser, testSubmission);
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            FileInputStream in = new FileInputStream(new File("docs/responseExamples/getStudyMetadata3.xml"));

            SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);
            this.metadata = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
