package nl.thehyve.ocdu.models.OcDefinitions;

import nl.thehyve.ocdu.models.OCEntities.Event;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.EventsType;
import org.openclinica.ws.beans.SiteRefType;
import org.openclinica.ws.beans.StudyRefType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class responsible for providing a {@link Map} with which a check can be performed if an event / event-repeat
 * is present in OpenClinica. Uses a list {@link StudySubjectWithEventsType} which is retrieved from OpenClinica and
 * converts it to a Map with a key consisting of the study identifier, the site identifier (optional), the eventOID
 * and the event-repeat number.
 * Created by Jacob Rousseau on 20-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class RegisteredEventInformation {

    /**
     * Creates a map of with as String as key which can be used to check if an event is present in OpenClinica. The value
     * set contains the event. The key consists of the study identifier, the site identifier (optional), the eventOID and the event
     * repeat number.
     * @param studySubjectWithEventsTypeList
     * @return
     */
    public static Map<String, EventResponseType> createEventKeyList(List<StudySubjectWithEventsType> studySubjectWithEventsTypeList) {
        Map<String, EventResponseType> ret = new HashMap<>(studySubjectWithEventsTypeList.size());
        for (StudySubjectWithEventsType studySubjectWithEventsType : studySubjectWithEventsTypeList) {
            EventsType eventsTypeList = studySubjectWithEventsType.getEvents();
            List<EventResponseType> eventList = eventsTypeList.getEvent();
            String subjectLabel = studySubjectWithEventsType.getLabel();
            for (EventResponseType eventResponseType : eventList) {
                StringBuffer buffer = new StringBuffer();
                StudyRefType studyRefType = studySubjectWithEventsType.getStudyRef();
                buffer.append(studyRefType.getIdentifier());
                SiteRefType siteRefType = studyRefType.getSiteRef();
                if (siteRefType != null) {
                    buffer.append(siteRefType.getIdentifier());
                }
                buffer.append(subjectLabel);
                buffer.append(eventResponseType.getEventDefinitionOID());
                buffer.append(eventResponseType.getOccurrence());
                ret.put(buffer.toString().toUpperCase(), eventResponseType);
            }
        }
        return ret;
    }

    public static Map<String, List<EventDefinition>> getMissingEventsPerSubject(MetaData metaData,
                                                                      List<StudySubjectWithEventsType> studySubjectWithEventsTypeList) {
        Map<String, List<EventDefinition>> ret = new HashMap<>();
        for (StudySubjectWithEventsType subjectWithEventsType : studySubjectWithEventsTypeList) {
            Set<String> existingEventOIDSet = new HashSet<>();
                subjectWithEventsType.getEvents().getEvent().forEach(eventResponseType -> existingEventOIDSet.add(eventResponseType.getEventDefinitionOID()));
            List<EventDefinition> missingEventsPerSubject =
                 metaData.getEventDefinitions().stream().
                         filter(eventDefinition -> (! existingEventOIDSet.contains(eventDefinition.getStudyEventOID()))).collect(Collectors.toList());
            ret.put(subjectWithEventsType.getLabel(), missingEventsPerSubject);
        }
        return ret;
    }
}
