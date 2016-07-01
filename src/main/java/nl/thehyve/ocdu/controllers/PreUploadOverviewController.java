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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller which provides the user with an overview of any remaining validation errors. This allows the user to
 * authorise the upload.
 * Created by Jacob Rousseau on 30-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
@Controller
@RequestMapping("/odm")
public class PreUploadOverviewController {

    @Autowired
    ValidationService validationService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    OcUserService ocUserService;


    @RequestMapping(value = "/pre-upload-overview", method = RequestMethod.GET)
    public ResponseEntity<Collection<ValidationErrorMessage>> retrieveOverviewValidation(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            String pwdHash = ocUserService.getOcwsHash(session);
            List<ValidationErrorMessage> patientsErrors = validationService.getPatientsErrors(currentUploadSession, pwdHash);
            List<ValidationErrorMessage> eventErrors = validationService.getEventsErrors(currentUploadSession, pwdHash);
            List<ValidationErrorMessage> dataErrors = validationService.getDataErrors(currentUploadSession, pwdHash);
            List<ValidationErrorMessage> accumulatedErrors = new ArrayList<>();
            accumulatedErrors.addAll(patientsErrors);
            accumulatedErrors.addAll(eventErrors);
            accumulatedErrors.addAll(dataErrors);
            // TODO add some kind of sorting to the accumulatedErrors to provide a user-friendly overview.
            return new ResponseEntity<>(accumulatedErrors, HttpStatus.OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
