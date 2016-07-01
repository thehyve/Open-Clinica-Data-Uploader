package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 17/05/16.
 */
public class EnumerationError extends  ValidationErrorMessage {
    public EnumerationError() {
        super("One or more values in your data is not present in the multiselect or codelist for that item.");
    }
}
