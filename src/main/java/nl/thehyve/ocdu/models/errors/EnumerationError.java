package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 17/05/16.
 */
public class EnumerationError extends  ValidationErrorMessage {
    public EnumerationError() {
        super("One or more of the MultipleSelect or CodeLists you referenced in your data contains unexpected values.");
    }
}
