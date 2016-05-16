package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 16/05/16.
 */
public class TooManySignificantDigits extends ValidationErrorMessage {
    public TooManySignificantDigits() {
        super("One or more of the values you provided contain more significant digits than it is allowed.");
    }
}
