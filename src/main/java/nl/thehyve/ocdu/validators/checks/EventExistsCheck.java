package nl.thehyve.ocdu.validators.checks;

import nl.thehyve.ocdu.models.*;
import nl.thehyve.ocdu.models.OCEntities.EventReference;
import nl.thehyve.ocdu.models.OCEntities.OcEntity;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static nl.thehyve.ocdu.models.ValidationErrorMessage.generateOffendingValueString;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class EventExistsCheck implements OcEntityCheck {

    private static final Logger log = LoggerFactory.getLogger(EventExistsCheck.class);

    @Override
    public ValidationErrorMessage getCorrespondingError(OcEntity data, MetaData metaData) {
        EventReference eventReference = (EventReference) data;
        String eventName = eventReference.getEventName();
        List<EventDefinition> eventDefinitions = metaData.getEventDefinitions();
        List<EventDefinition> matching = eventDefinitions.stream()
                .filter(evDef -> evDef.getName().equals(eventName)).collect(Collectors.toList());
        if (matching.size() == 1) {
            return null;
        } else if (matching.size() == 0) {
            ValidationErrorMessage error = new ValidationErrorMessage("Event does not exist");
            error.addOffendingValue(generateOffendingValueString(data, eventName));
            error.setError(true); // this is not a warning, it is an error
            return error;
        } else {
            log.error("Event: "+ eventName+" has multiple definitions in: "+ metaData.getStudyIdentifier());
            return null;
        }
    }
}
