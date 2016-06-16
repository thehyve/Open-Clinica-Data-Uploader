package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 15/06/16.
 */
public class RepeatInNonrepeatingItem extends ValidationErrorMessage {
    public RepeatInNonrepeatingItem() {
        super("One or more of the items from your submission contains repeat ordinal while item belongs " +
                "to a non-repeating group");
    }
}
