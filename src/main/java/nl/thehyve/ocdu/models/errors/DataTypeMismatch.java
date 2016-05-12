package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 12/05/16.
 */
public class DataTypeMismatch extends ValidationErrorMessage {
    public DataTypeMismatch() {
        super("One or more items you provided do not match the expected type.");
    }
}
