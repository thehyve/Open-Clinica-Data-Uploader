package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.OCEnvironmentsConfig;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.UploadSessionRepository;
import nl.thehyve.ocdu.repositories.OCUserRepository;
import nl.thehyve.ocdu.services.DataService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.UploadSessionNotFoundException;
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
        try {
            UploadSession submission = uploadSessionService.getCurrentUploadSession(session);
            DataService.FieldsDetermined info = dataService.getInfo(submission);
            if (info == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(info, OK);
            }
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/update", method=RequestMethod.POST)
    public ResponseEntity<?> updateSubmission(@RequestParam(value = "name") String step  ,HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            UploadSession.Step step1 = UploadSession.Step.valueOf(step);
            if (step1 != null) {
                currentUploadSession.setStep(step1);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    @RequestMapping(value = "/delete", method=RequestMethod.POST)
    public ResponseEntity<?> deleteSubmission(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(currentUploadSession);
            clinicalDataRepository.delete(bySubmission);
            uploadSessionRepository.delete(currentUploadSession);
            uploadSessionService.setCurrentUploadSession(session, null);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/deleteSession", method = RequestMethod.POST)
    public ResponseEntity<?> deleteSubmissionById(@RequestParam(value = "id") String sessionId, HttpSession session) {
        try {
            long longsessionid = Long.getLong(sessionId);
            UploadSession selectedSession = uploadSessionRepository.findOne(longsessionid);
            if(selectedSession != null) {
                OcUser currentOcUser = ocUserService.getCurrentOcUser(session);
                long userId1 = currentOcUser.getId();
                long userId2 = selectedSession.getOwner().getId();
                if(userId1 == userId2) {
                    List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(selectedSession);
                    clinicalDataRepository.delete(bySubmission);
                    uploadSessionRepository.delete(selectedSession);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<List<UploadSession>> unfinishedSessions(HttpSession session) {
        try {
            OcUser ocUser = ocUserService.getCurrentOcUser(session);
            List uploadSessions = uploadSessionRepository.findByOwner(ocUser);

            return new ResponseEntity<>(uploadSessions, OK);
        } catch (UploadSessionNotFoundException ex) {
            System.out.println(ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST) //TODO: add tests
    public ResponseEntity<?> createUploadSession(@RequestParam(value = "name", defaultValue = "newSession") String name,
                                                 HttpSession session) {
        try {
            OcUser usr = ocUserService.getCurrentOcUser(session);
            UploadSession uploadSession = new UploadSession(name, UploadSession.Step.MAPPING, new Date(), usr);//TODO: Add remaining steps
            uploadSessionRepository.save(uploadSession);
            uploadSessionService.setCurrentUploadSession(session, uploadSession);
            return new ResponseEntity<>(OK);
        } catch (UploadSessionNotFoundException ex) {
            System.out.println(ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public ResponseEntity<UploadSession> currentSession(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            return new ResponseEntity<>(currentUploadSession, OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public ResponseEntity<?> selectSession(@RequestParam(value = "sessionId") Long sessionId, HttpSession session) {
        try {
            UploadSession requested = uploadSessionRepository.findOne(sessionId);
            if (requested.getOwner().getId() == ocUserService.getCurrentOcUser(session).getId()) {
                uploadSessionService.setCurrentUploadSession(session, requested);
                return new ResponseEntity<>(OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (UploadSessionNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/user-items")
    public ResponseEntity<List<String>> getUserItems(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            List<String> userItems = dataService.getUserItems(currentUploadSession);
            return new ResponseEntity<>(userItems, OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

}
