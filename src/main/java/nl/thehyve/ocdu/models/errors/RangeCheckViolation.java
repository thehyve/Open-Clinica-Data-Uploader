package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
public class RangeCheckViolation extends ValidationErrorMessage {
    public RangeCheckViolation() {
        super("One or more values in your data are outside of the range for that item");
    }
}
