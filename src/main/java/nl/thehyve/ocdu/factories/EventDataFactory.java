package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.RegisteredEventInformation;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 */
public class EventDataFactory extends UserSubmittedDataFactory {

    public static final String STUDY_SUBJECT_ID = "StudySubjectID";
    public static final String EVENT_NAME = "EventName";
    public static final String STUDY = "Study";
    public static final String SITE = "Site";
    public static final String LOCATION = "Location";
    public static final String START_DATE = "StartDate";
    public static final String START_TIME = "StartTime";
    public static final String END_DATE = "EndDate";
    public static final String END_TIME = "EndTime";
    public static final String REPEAT_NUMBER = "RepeatNumber";

    public EventDataFactory(OcUser user, UploadSession submission) {
        super(user, submission);
    }

    public final static String[] MANDATORY_HEADERS =
            {STUDY_SUBJECT_ID, EVENT_NAME, STUDY, SITE, START_DATE, REPEAT_NUMBER};
    public final static String[] POSITIVE_INTEGERS = { REPEAT_NUMBER };

    public List<Event> createEventsData(Path tabularFilePath) {
        Optional<String[]> headerRow = getHeaderRow(tabularFilePath);
        if (headerRow.isPresent()) {
            Map<String, Integer> columnsIndex = createColumnsIndexMap(headerRow.get());

            try (Stream<String> lines = Files.lines(tabularFilePath)) {
                return lines.skip(1)
                        .map(UserSubmittedDataFactory::parseLine)
                        .map(row -> mapRow(row, columnsIndex))
                        //TODO Maybe we should return stream instead?
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            throw new RuntimeException("File appears to be empty.");
        }
    }

    protected Event mapRow(String[] row, Map<String, Integer> columnsIndex) {
        Event event = new Event();
        event.setOwner(getUser());
        event.setSubmission(getSubmission());
        setValue(row, columnsIndex, STUDY_SUBJECT_ID, event::setSsid);
        setValue(row, columnsIndex, EVENT_NAME, event::setEventName);
        setValue(row, columnsIndex, STUDY, event::setStudy);
        setValue(row, columnsIndex, SITE, event::setSite);
        setValue(row, columnsIndex, LOCATION, event::setLocation);
        setValue(row, columnsIndex, START_DATE, event::setStartDate);
        setValue(row, columnsIndex, START_TIME, event::setStartTime);
        setValue(row, columnsIndex, END_DATE, event::setEndDate);
        setValue(row, columnsIndex, END_TIME, event::setEndTime);
        setValue(row, columnsIndex, REPEAT_NUMBER, event::setRepeatNumber);
        return event;
    }

    protected void setValue(String[] row, Map<String, Integer> columnsIndex, String columnName,
                            Consumer<String> consumer) {
        if (!columnsIndex.containsKey(columnName)) {
            return;
        }
        String cellValue = row[columnsIndex.get(columnName)];
        consumer.accept(cellValue);
    }

    public List<String> generateEventSchedulingTemplate(MetaData metaData, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList) {
        Map<String, List<EventDefinition>> eventsPerSubject = RegisteredEventInformation.getMissingEventsPerSubject(metaData, studySubjectWithEventsTypeList);

        List<String> result = new ArrayList<>();
        String delim = "\t";
        List<String> header = new ArrayList<>();
        header.add("Study Subject ID");
        header.add("Event Name");
        header.add("Study");
        header.add("Location");
        header.add("Start Date");
        header.add("Start Time");
        header.add("End Date");
        header.add("End Time");
        header.add("Repeat Number");
        result.add(String.join(delim, header) + "\n");

        for(String ssid: eventsPerSubject.keySet()) {
            List<EventDefinition> events = eventsPerSubject.get(ssid);
            for(EventDefinition ed: events) {
                String eventname = ed.getName();
                List<String> row = new ArrayList<>();
                row.add(ssid);//study subject id
                row.add(eventname);//event name
                row.add("");//study
                row.add("");//location
                row.add("");//Start Date
                row.add("");//Start Time
                row.add("");//End Date
                row.add("");//End Time
                row.add("");//Repeat Number
                result.add(String.join(delim, row) + "\n");
            }//for
        }//for

        return result;
    }
}
