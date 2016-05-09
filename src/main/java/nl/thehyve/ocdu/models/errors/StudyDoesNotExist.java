package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 09/05/16.
 */
public class StudyDoesNotExist extends ValidationErrorMessage {
    public StudyDoesNotExist() {
        super("One or more study references you used is not recognized by Open Clinica - it does not exist.");
    }
}
