package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class MandatoryItemInCrfMissing extends ValidationErrorMessage {
    public MandatoryItemInCrfMissing() {
        super("One or more items do not match the expected data type. " +
                "Items with type REAL need to have a decimal mark (e.g. not 1 but 1.0).");
    }
}
