package nl.thehyve.ocdu.models.OcDefinitions;

import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.EventsType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
    public static Map<String, EventResponseType> createEventKeyList(String studyIdentifier, String siteIdentifier,  List<StudySubjectWithEventsType> studySubjectWithEventsTypeList) {
        Map<String, EventResponseType> ret = new HashMap<>(studySubjectWithEventsTypeList.size());
        for (StudySubjectWithEventsType studySubjectWithEventsType : studySubjectWithEventsTypeList) {
            EventsType eventsTypeList = studySubjectWithEventsType.getEvents();
            List<EventResponseType> eventList = eventsTypeList.getEvent();
            String subjectLabel = studySubjectWithEventsType.getLabel();
            for (EventResponseType eventResponseType : eventList) {
                StringBuffer buffer = new StringBuffer();

                buffer.append(studyIdentifier);
                if (! StringUtils.isEmpty(siteIdentifier)) {
                    buffer.append(siteIdentifier);
                }
                buffer.append(subjectLabel);
                buffer.append(eventResponseType.getEventDefinitionOID());
                buffer.append(eventResponseType.getOccurrence());
                ret.put(buffer.toString().toUpperCase(), eventResponseType);
            }
        }
        return ret;
    }
}
