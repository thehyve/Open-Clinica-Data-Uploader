package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.repositories.SubjectRepository;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.OpenClinicaService;
import nl.thehyve.ocdu.services.UploadSessionNotFoundException;
import nl.thehyve.ocdu.services.UploadSessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

/**
 *
 * Calls related to subjects registration.
 *
 * Created by piotrzakrzewski on 20/06/16.
 */
@RestController
@RequestMapping("/subjects")
public class SubjectsController {

    @Autowired
    OpenClinicaService openClinicaService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    OcUserService ocUserService;

    @Autowired
    SubjectRepository subjectRepository;

    /**
     * Triggers registration of subjects submitted by the user. Does not do validation.
     * @param session
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResponseEntity<String> checkNewPatients(HttpSession session) { //TODO: change all HTTP calls to accept at least submission id - so that running two submissions at the same time is possible in one browser
        try {
            UploadSession uploadSession = uploadSessionService.getCurrentUploadSession(session);
            OcUser user = ocUserService.getCurrentOcUser(session);
            String username = user.getUsername();
            String pwdHash = ocUserService.getOcwsHash(session);
            String url = user.getOcEnvironment();
            Collection<Subject> subjects = subjectRepository.findBySubmission(uploadSession);
            if (! subjects.isEmpty()) {
                Collection<ValidationErrorMessage> result = openClinicaService.registerPatients(username, pwdHash, url, subjects);
                if (result.isEmpty()) {
                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                return new ResponseEntity(result, HttpStatus.OK);
            }

        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }

}
