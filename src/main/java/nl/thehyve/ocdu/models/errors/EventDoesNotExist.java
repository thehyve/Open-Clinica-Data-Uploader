package nl.thehyve.ocdu.models.errors;

import nl.thehyve.ocdu.services.ValidationService;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class EventDoesNotExist extends ValidationErrorMessage {
    public EventDoesNotExist(String generalMessage) {
        super(generalMessage);
    }
}
