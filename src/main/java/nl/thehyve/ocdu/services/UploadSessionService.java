package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.UploadSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * User submission aka UploadSession related methods.
 *
 * Created by piotrzakrzewski on 06/05/16.
 */
@Service
public class UploadSessionService {

    @Autowired
    UploadSessionRepository uploadSessionRepository;

    private static final String CURRENT_SESSION_ATTRIBUTE = "currentOcUploadSession";

    public UploadSession getCurrentUploadSession(HttpSession session) throws UploadSessionNotFoundException {
        UploadSession curUploadSession = (UploadSession) session.getAttribute(CURRENT_SESSION_ATTRIBUTE);
        if (curUploadSession == null || curUploadSession.getOwner() == null) {
            throw new UploadSessionNotFoundException();
        }
        return curUploadSession;
    }

    public void setCurrentUploadSession(HttpSession session, UploadSession ocSession) {
        session.setAttribute(CURRENT_SESSION_ATTRIBUTE, ocSession);
    }
}
