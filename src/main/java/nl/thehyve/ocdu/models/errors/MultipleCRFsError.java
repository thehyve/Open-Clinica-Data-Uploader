package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 01/07/16.
 */
public class MultipleCRFsError extends ValidationErrorMessage {
    public MultipleCRFsError() {
        super("Rows in the data file can use only one CRF and one CRF version");
    }
}
