package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.IncorrectNumberOfEvents;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class MultipleEventsCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        Set<String> eventsUsed = data.stream().map(clinicalData -> clinicalData.getEventName()).collect(Collectors.toSet());
        if (eventsUsed.size() != 1) {
            IncorrectNumberOfEvents error = new IncorrectNumberOfEvents();
            if (eventsUsed.size() == 0)
                error.addOffendingValue("No events referenced in the data");
            else {
                eventsUsed.forEach(eventName ->
                        error.addOffendingValue(eventName));
            }
            return error;
        } else return null;
    }
}
