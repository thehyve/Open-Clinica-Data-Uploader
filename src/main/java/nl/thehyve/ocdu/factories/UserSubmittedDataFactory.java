package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class UserSubmittedDataFactory {

    public final static String FILE_SEPARATOR = "\t";

    private final UploadSession submission;
    private final OcUser user;

    public UserSubmittedDataFactory(OcUser user, UploadSession submission) {
        this.user = user;
        this.submission = submission;
    }

    public UploadSession getSubmission() {
        return submission;
    }

    public OcUser getUser() {
        return user;
    }
}
