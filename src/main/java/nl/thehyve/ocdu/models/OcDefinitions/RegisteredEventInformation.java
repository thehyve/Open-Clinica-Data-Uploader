package nl.thehyve.ocdu.models.OcDefinitions;

import nl.thehyve.ocdu.models.OCEntities.Event;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.EventsType;
import org.openclinica.ws.beans.SiteRefType;
import org.openclinica.ws.beans.StudyRefType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.Collection;
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
     *
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

    public static Collection<Event> determineEventsToSchedule(MetaData metaData,
                                                              List<StudySubjectWithEventsType> studySubjectWithEventsTypeList,
                                                              Set<ImmutablePair> patientsInEvent) {
        Collection<Event> ret = new HashSet<>();

        Map<String, String> eventNameToOIDMap = new HashMap<>();
        Map<String, String> eventOIDToNameMap = new HashMap<>();
        metaData.getEventDefinitions().forEach(eventDefinition -> {
            eventNameToOIDMap.put(eventDefinition.getName(), eventDefinition.getStudyEventOID());
            eventOIDToNameMap.put(eventDefinition.getStudyEventOID(), eventDefinition.getName());
        });

        Collection<Event> alreadyRegistered = new HashSet<>();
        for (StudySubjectWithEventsType studySubjectWithEventsType : studySubjectWithEventsTypeList) {
            List<EventResponseType> regEvents = studySubjectWithEventsType.getEvents().getEvent();
            String studySubjectID = studySubjectWithEventsType.getSubject().getUniqueIdentifier();
            for (EventResponseType eventResponseType : regEvents) {
                String eventOID = eventResponseType.getEventDefinitionOID();
                String eventName = eventOIDToNameMap.get(eventOID);
                String eventRepeatNumber = eventResponseType.getOccurrence();
                Event event = new Event();
                event.setEventName(eventName);
                event.setRepeatNumber(eventRepeatNumber);
                event.setSsid(studySubjectID);
                alreadyRegistered.add(event);
            }
        }

        patientsInEvent.stream().forEach(patientInEvent -> {
            String studySubjectID = (String) patientInEvent.left;
            String eventName = (String) patientInEvent.right;
            String eventRepeatNumber = StringUtils.substringAfterLast(eventName, "#");
            if (StringUtils.isEmpty(eventRepeatNumber)) {
                eventRepeatNumber = "1";
            }
            eventName = StringUtils.substringBeforeLast(eventName, "#");
            Event eventToSchedule = new Event();
            eventToSchedule.setRepeatNumber(eventRepeatNumber);
            eventToSchedule.setSsid(studySubjectID);
            eventToSchedule.setEventName(eventName);
            if (! alreadyRegistered.contains(eventToSchedule)) {
               ret.add(eventToSchedule);
            }
        });
        return ret;
    }
}
