package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.UploadSessionNotFoundException;
import nl.thehyve.ocdu.services.UploadSessionService;
import nl.thehyve.ocdu.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by piotrzakrzewski on 06/05/16.
 */
@RestController
@RequestMapping("/validate")
public class ValidationController {

    @Autowired
    ValidationService validationService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    OcUserService ocUserService;

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public ResponseEntity<List<ValidationErrorMessage>> validateData(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            String pwdHash = ocUserService.getOcwsHash(session);
            List<ValidationErrorMessage> dataErrors = validationService.getDataErrors(currentUploadSession, pwdHash);
            return new ResponseEntity<>(dataErrors, HttpStatus.OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    public ResponseEntity<List<ValidationErrorMessage>> validatePatients(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            String pwdHash = ocUserService.getOcwsHash(session);
            List<ValidationErrorMessage> patientsErrors = validationService.getPatientsErrors(currentUploadSession, pwdHash);
            return new ResponseEntity<>(patientsErrors, HttpStatus.OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public ResponseEntity<List<ValidationErrorMessage>> validateEvents(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            String pwdHash = ocUserService.getOcwsHash(session);
            List<ValidationErrorMessage> eventsErrors = validationService.getEventsErrors(currentUploadSession, pwdHash);
            return new ResponseEntity<>(eventsErrors,HttpStatus.OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
