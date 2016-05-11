package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class FieldLengthExceeded  extends ValidationErrorMessage{
    public FieldLengthExceeded() {
        super("One or more of your items exceeds allowed length.");
    }
}
