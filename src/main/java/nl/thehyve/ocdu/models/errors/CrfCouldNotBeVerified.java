package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 10/05/16.
 */

/**
 * Created by piotrzakrzewski on 10/05/16.
 */
public class CrfCouldNotBeVerified extends ValidationErrorMessage {

    public CrfCouldNotBeVerified() {
        super("One or more Clinical Report Forms you used could not be verified due to incorrect or missing event name");
    }
}
