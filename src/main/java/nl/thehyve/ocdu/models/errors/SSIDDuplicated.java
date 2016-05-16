package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 16/05/16.
 */
public class SSIDDuplicated extends ValidationErrorMessage {
    public SSIDDuplicated() {
        super("One or more of the Study Subject Identifiers are " +
                "used more than once for the same event, repeat, CRF and CRF version.");
    }
}
