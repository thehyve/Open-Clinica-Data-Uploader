package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.OCEnvironmentsConfig;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.UploadSessionRepository;
import nl.thehyve.ocdu.repositories.OCUserRepository;
import nl.thehyve.ocdu.services.DataService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.UploadSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

/**
 * Created by bo on 4/26/16.
 */
@RestController
@RequestMapping("/submission")
public class UploadSessionController {

    private static final Logger log = LoggerFactory.getLogger(UploadSessionController.class);

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    public String username() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<DataService.FieldsDetermined> getFieldsInfo(HttpSession session) {
        UploadSession submission = uploadSessionService.getCurrentUploadSession(session);
        DataService.FieldsDetermined info = dataService.getInfo(submission);
        if (info == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(info, OK);
        }
    }

    @Autowired
    DataService dataService;

    @Autowired
    OcUserService ocUserService;

    @Autowired
    UploadSessionRepository uploadSessionRepository;

    @Autowired
    UploadSessionService uploadSessionService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<UploadSession> unfinishedSessions(HttpSession session) {
        OcUser ocUser = ocUserService.getCurrentOcUser(session);
        List uploadSessions = uploadSessionRepository.findByOwner(ocUser);
        if (uploadSessions == null) {
            log.error("Attempted retrieving uploadedSessions before any was created.");
            return new ArrayList<>();
        }
        return uploadSessions;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST) //TODO: add tests
    public void createUploadSession(@RequestParam(value = "name", defaultValue = "newSession") String name,
                                    HttpSession session) {
        OcUser usr = ocUserService.getCurrentOcUser(session);
        UploadSession uploadSession = new UploadSession(name, UploadSession.Step.MAPPING, new Date(), usr);//TODO: Add remaining steps
        uploadSessionRepository.save(uploadSession);
        uploadSessionService.setCurrentUploadSession(session, uploadSession);
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public UploadSession currentSession(HttpSession session) {
        return uploadSessionService.getCurrentUploadSession(session);
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public ResponseEntity<?> selectSession(@RequestParam(value = "sessionId") Long sessionId, HttpSession session) {
        UploadSession requested = uploadSessionRepository.findOne(sessionId);
        if (requested.getOwner().getId() == ocUserService.getCurrentOcUser(session).getId()) {
            uploadSessionService.setCurrentUploadSession(session, requested);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //TODO: Make distinction between unauthorized and requested session not existent errors
        }
    }

}
