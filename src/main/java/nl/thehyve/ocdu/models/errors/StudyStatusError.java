package nl.thehyve.ocdu.models.errors;


/**
 * Created by piotrzakrzewski on 22/06/16.
 */
public class StudyStatusError extends ValidationErrorMessage {
    public StudyStatusError() {
        super("Study status does not allow data upload - " +
                "select different study or change status in OpenClinica before proceeding.");
    }
}
