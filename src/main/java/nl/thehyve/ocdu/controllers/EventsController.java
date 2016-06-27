package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.EventRepository;
import nl.thehyve.ocdu.services.DataService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.OpenClinicaService;
import nl.thehyve.ocdu.services.UploadSessionService;
import nl.thehyve.ocdu.validators.EventDataOcChecks;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Jacob Rousseau on 22-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
@RestController
@RequestMapping("/events")
public class EventsController {

    @Autowired
    OpenClinicaService openClinicaService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    OcUserService ocUserService;

    @Autowired
    DataService dataService;

    @Autowired
    EventRepository eventRepository;


    @RequestMapping(value = "/register-event", method = RequestMethod.POST)
    public ResponseEntity<String> registerEvents(HttpSession session) {
        try {
            UploadSession uploadSession = uploadSessionService.getCurrentUploadSession(session);
            OcUser user = ocUserService.getCurrentOcUser(session);
            String username = user.getUsername();
            String pwdHash = ocUserService.getOcwsHash(session);
            String url = user.getOcEnvironment();
            MetaData metaData = dataService.getMetaData(uploadSession, pwdHash);
            Study study = dataService.findStudy(uploadSession.getStudy(), user, username);

            List<Event> eventList = eventRepository.findBySubmission(uploadSession);
            EventDataOcChecks eventDataOcChecks = new EventDataOcChecks(metaData, eventList);
            List<ValidationErrorMessage> validationErrorMessageList = eventDataOcChecks.getErrors();
            if (! validationErrorMessageList.isEmpty()) {
                return new ResponseEntity(validationErrorMessageList, HttpStatus.BAD_REQUEST);
            }

            List<StudySubjectWithEventsType> subjectWithEventsTypes = openClinicaService
                    .getStudySubjectsType(username, pwdHash, url, study.getIdentifier(), "");

            openClinicaService.scheduleEvents(username, pwdHash, url, metaData, eventList, subjectWithEventsTypes);
            return new ResponseEntity<>("", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }
}
