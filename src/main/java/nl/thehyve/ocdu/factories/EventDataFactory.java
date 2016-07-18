package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.EventToSchedule;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.ProtocolFieldRequirementSetting;
import nl.thehyve.ocdu.models.OcDefinitions.RegisteredEventInformation;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 */
public class EventDataFactory extends UserSubmittedDataFactory {

    public static final String STUDY_SUBJECT_ID = "Study Subject ID";
    public static final String EVENT_NAME = "Event Name";
    public static final String STUDY = "Study";
    public static final String SITE = "Site";
    public static final String LOCATION = "Location";
    public static final String START_DATE = "Start Date";
    public static final String START_TIME = "Start Time";
    public static final String END_DATE = "End Date";
    public static final String END_TIME = "End Time";
    public static final String REPEAT_NUMBER = "Repeat Number";

    public EventDataFactory(OcUser user, UploadSession submission) {
        super(user, submission);
    }

    public final static String[] MANDATORY_HEADERS =
            {STUDY_SUBJECT_ID, EVENT_NAME, STUDY, START_DATE, REPEAT_NUMBER};
    public final static String[] POSITIVE_INTEGERS = {REPEAT_NUMBER};

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
        String[] _row = new String[columnsIndex.size()];
        if (row.length == columnsIndex.size()) _row = row;
        else {
            for (int i = 0; i < _row.length; i++) {
                if (i < row.length) {
                    _row[i] = row[i];
                } else {
                    _row[i] = "";
                }
            }
        }

        Event event = new Event();
        event.setOwner(getUser());
        event.setSubmission(getSubmission());
        setValue(_row, columnsIndex, STUDY_SUBJECT_ID, event::setSsid);
        setValue(_row, columnsIndex, EVENT_NAME, event::setEventName);
        setValue(_row, columnsIndex, STUDY, event::setStudy);
        setValue(_row, columnsIndex, SITE, event::setSite);
        setValue(_row, columnsIndex, LOCATION, event::setLocation);
        setValue(_row, columnsIndex, START_DATE, event::setStartDate);
        setValue(_row, columnsIndex, START_TIME, event::setStartTime);
        setValue(_row, columnsIndex, END_DATE, event::setEndDate);
        setValue(_row, columnsIndex, END_TIME, event::setEndTime);
        setValue(_row, columnsIndex, REPEAT_NUMBER, event::setRepeatNumber);
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

    public List<String> generateEventSchedulingTemplate(MetaData metaData, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Set<ImmutablePair> patientsInEvent) {
        Collection<EventToSchedule> eventToScheduleList =
                RegisteredEventInformation.determineEventsToSchedule(metaData, studySubjectWithEventsTypeList, patientsInEvent);

        List<String> result = new ArrayList<>();
        String delim = "\t";
        List<String> header = new ArrayList<>();
        header.add("Study Subject ID");
        header.add("Event Name");
        header.add("Study");
        if (isLocationInTemplate(metaData)) {
            header.add("Location");
        }
        header.add("Start Date");
        header.add("Start Time");
        header.add("End Date");
        header.add("End Time");
        header.add("Repeat Number");
        result.add(String.join(delim, header) + "\n");

        for (EventToSchedule eventToSchedule : eventToScheduleList) {
                List<String> row = new ArrayList<>();
                row.add(eventToSchedule.getStudySubjectID());//study subject id
                row.add(eventToSchedule.getEventName());//event name
                row.add(metaData.getStudyName());//study
                if (isLocationInTemplate(metaData)) {
                    row.add("");//location
                }
                row.add("");//Start Date
                row.add("");//Start Time
                row.add("");//End Date
                row.add("");//End Time
                row.add(eventToSchedule.getRepeatNumber());//Repeat Number
                result.add(String.join(delim, row) + "\n");
        }
        return result;
    }

    private boolean isLocationInTemplate(MetaData metaData) {
        ProtocolFieldRequirementSetting locationRequirementSetting = metaData.getLocationRequirementSetting();
        if (locationRequirementSetting.equals(ProtocolFieldRequirementSetting.BANNED)) {
            return false;
        }
        return true;
    }
}
