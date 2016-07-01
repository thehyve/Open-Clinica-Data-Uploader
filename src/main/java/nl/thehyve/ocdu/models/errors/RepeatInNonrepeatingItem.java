package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 15/06/16.
 */
public class RepeatInNonrepeatingItem extends ValidationErrorMessage {
    public RepeatInNonrepeatingItem() {
        super("One or more items are indicated as repeated, but are non-repeating in OpenClinica");
    }
}
