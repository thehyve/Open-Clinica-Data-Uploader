package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class MandatoryItemInCrfMissing extends ValidationErrorMessage {
    public MandatoryItemInCrfMissing() {
        super("One or more items mandatory in CRFs you used are missing.");
    }
}
