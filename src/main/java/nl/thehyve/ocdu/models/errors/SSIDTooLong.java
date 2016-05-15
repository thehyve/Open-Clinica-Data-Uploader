package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class SSIDTooLong extends ValidationErrorMessage {
    public SSIDTooLong(int allowedMax) {
        super("One or more of the SSIDs you defined is too long. Max length: " + allowedMax);
    }
}
