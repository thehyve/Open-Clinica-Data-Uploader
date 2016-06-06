package nl.thehyve.ocdu.models.errors;

/**
 * Created by jacob on 6/6/16.
 */
public class CRFVersionMismatchError extends ValidationErrorMessage {

    public CRFVersionMismatchError() {
        super("Mismatching Clinical Report Forms version(s) found.");
    }

}
