package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.RepeatInNonrepeatingEvent;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 17/05/16.
 */
public class EventRepeatCrossCheck implements ClinicalDataCrossCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        // if  there is a non repeating event which has repeat higher than 1 return error
        RepeatInNonrepeatingEvent error = new RepeatInNonrepeatingEvent();
        Set<String> offenders = data.stream().filter(clinicalData -> clinicalData.getEventRepeat() > 1)
                .filter(clinicalData -> isViolator(clinicalData, metaData))
                .map(clinicalData -> "Event " + clinicalData.getEventName() + " repeat: " + clinicalData.getEventRepeat())
                .collect(Collectors.toSet());
        offenders.forEach(offender -> error.addOffendingValue(offender));
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }

    private boolean isViolator(ClinicalData clinicalData, MetaData metaData) {
        return !isRepeating(clinicalData.getEventName(), metaData);
    }

    private boolean isRepeating(String eventName, MetaData metaData) {
        return metaData.getEventDefinitions().stream()
                .anyMatch(eventDefinition -> eventDefinition.getName().equals(eventName)
                        && eventDefinition.isRepeating());
    }

}
