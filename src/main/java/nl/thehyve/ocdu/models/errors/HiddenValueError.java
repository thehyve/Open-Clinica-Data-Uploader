package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 08/06/16.
 */
public class HiddenValueError extends ValidationErrorMessage {
    public HiddenValueError() {
        super("One or more items in your file is hidden but contains non-empty value.");
    }
}
