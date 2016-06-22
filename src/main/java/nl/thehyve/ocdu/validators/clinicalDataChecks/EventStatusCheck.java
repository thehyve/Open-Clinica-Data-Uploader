package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.EventStatusNotAllowed;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.EventsType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 22/06/16.
 */
public class EventStatusCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        ValidationErrorMessage error = new EventStatusNotAllowed();
        Set<String> offenders = new HashSet<>();
        Set<String> eventsInData = eventMap.keySet();
        Set<String> evOidsInData = eventOIDsInData(metaData, eventsInData);
        studySubjectWithEventsTypeList.stream().forEach(studySubjectWithEventsType -> {
            EventsType eventsWrapper = studySubjectWithEventsType.getEvents();
            List<EventResponseType> events = eventsWrapper.getEvent();
            events.stream().forEach(eventResponseType -> {
                String status = eventResponseType.getStatus();
                if (isInvalidStatus(status) && evOidsInData.contains(eventResponseType.getEventDefinitionOID())) {
                    offenders.add("Event: " + eventResponseType.getEventDefinitionOID() + " has status: " + status);
                }
            });
        });
        error.addAllOffendingValues(offenders);
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }

    private boolean isInvalidStatus(String status) {
        if (!status.equals("available")) {
            return true;
        } else return false;
    }

    private Set<String> eventOIDsInData(MetaData metaData, Set<String> usedEventNames) {
        Set<String> usedEventOIDs = metaData.getEventDefinitions()
                .stream().filter(eventDefinition -> usedEventNames.contains(eventDefinition.getName()))
                .map(eventDefinition -> eventDefinition.getStudyEventOID()).collect(Collectors.toSet());
        return usedEventOIDs ;
    }
}
