package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 17/05/16.
 */
public class RepeatInNonrepeatingEvent extends ValidationErrorMessage {
    public RepeatInNonrepeatingEvent() {
        super("One or more rows in your data reference a repeat higher than 1 for a nonrepeating event.");
    }
}
