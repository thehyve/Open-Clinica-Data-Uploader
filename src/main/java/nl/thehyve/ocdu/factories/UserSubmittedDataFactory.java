package nl.thehyve.ocdu.factories;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class UserSubmittedDataFactory {

    public final static String FILE_SEPARATOR = "\t";

    private final String submission;
    private final String userName;

    public UserSubmittedDataFactory(String userName, String submission) {
        this.userName = userName;
        this.submission = submission;
    }

    public String getSubmission() {
        return submission;
    }

    public String getUserName() {
        return userName;
    }
}
