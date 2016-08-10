package nl.thehyve.ocdu.models.errors;

/**
 * Error which indicates that a gap is present in the event repeats.
 * Created by jacob on 8/1/16.
 */
public class EventGapError extends ValidationErrorMessage {

    public EventGapError() {
        super("A repeating event will be created with a gap in the event sequence numbers or does not start with the first repeat.");
    }
}
