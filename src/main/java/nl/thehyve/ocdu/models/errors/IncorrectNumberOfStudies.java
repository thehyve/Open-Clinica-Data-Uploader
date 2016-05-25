package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class IncorrectNumberOfStudies extends ValidationErrorMessage {
    public IncorrectNumberOfStudies() {
        super("Data file must reference one and only study name.");
    }
}
