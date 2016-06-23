package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class IncorrectNumberOfEvents extends ValidationErrorMessage {
    public IncorrectNumberOfEvents() {
        super("One (and only one) event is allowed per data file.");
    }
}
