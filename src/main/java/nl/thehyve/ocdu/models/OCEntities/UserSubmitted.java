package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public interface UserSubmitted {
    OcUser getOwner();
    UploadSession getSubmission();
    void setOwner(OcUser owner);
    void setSubmission(UploadSession submission);
}
