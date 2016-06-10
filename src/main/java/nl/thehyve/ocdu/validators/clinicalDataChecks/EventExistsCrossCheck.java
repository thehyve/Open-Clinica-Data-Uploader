package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.EventDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class EventExistsCrossCheck implements ClinicalDataCrossCheck {

    private static final Logger log = LoggerFactory.getLogger(EventExistsCrossCheck.class);

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, List<StudySubjectWithEventsType> subjectWithEventsTypeList) {
        List<String> validEventNames = new ArrayList<>();
        metaData
                .getEventDefinitions().stream()
                .forEach(eventDefinition -> validEventNames.add(eventDefinition.getName()));
        List<ClinicalData> violators = data.stream()
                .filter(clinicalData -> !validEventNames.contains(clinicalData.getEventName()))
                .collect(Collectors.toList());
        if (violators.size() > 0) {
            ValidationErrorMessage error =
                    new EventDoesNotExist();
            List<String> nonExistentEventNames = new ArrayList<>();
            violators.stream().forEach(clinicalData ->
            { String evName = clinicalData.getEventName();
                if(!nonExistentEventNames.contains(evName))nonExistentEventNames.add(evName);
            });
            error.addAllOffendingValues(nonExistentEventNames);
            return error;
        } else return null;
    }
}
