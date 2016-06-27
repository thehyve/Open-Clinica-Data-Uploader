package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
public class TooManyValues extends ValidationErrorMessage {
    public TooManyValues() {
        super("One or more of the values you submitted contain more than one comma " +
                "separated values while its data type does not allow that - please mind that real number must use \".\"" +
                "as a decimal mark, not comma");
    }
}
