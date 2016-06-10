package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.factories.EventDataFactory;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class EventsFileValidator extends GenericFileValidator {
    public EventsFileValidator() {
        super(EventDataFactory.MANDATORY_HEADERS, EventDataFactory.POSITIVE_INTEGERS);
    }
}
