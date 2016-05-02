package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.OcUserDetails;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.UploadSessionRepository;
import nl.thehyve.ocdu.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bo on 4/26/16.
 */
@RestController
public class UploadSessionController {

    private static final Logger log = LoggerFactory.getLogger(UploadSessionController.class);

    @RequestMapping(value="/username", method= RequestMethod.GET)
    public String username() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username; //TODO: FIX:does not return JSON
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    UploadSessionRepository uploadSessionRepository;

    @RequestMapping(value="/unfinished-sessions", method= RequestMethod.GET)
    public List<UploadSession> unfinishedSessions(HttpSession session) {
        String ocEnv = (String) session.getAttribute("ocEnvironment");
        OcUser ocUser = getOcUser(ocEnv);
        List uploadSessions = ocUser.getUploadSession();
        if (uploadSessions == null) {
            log.error("Attempted retrieving uploadedSessions before any was created.");
            return new ArrayList<>();
        }
        return uploadSessions;
    }

    @RequestMapping(value = "/create-session",method = RequestMethod.POST) //TODO: add tests
    public void createUploadSession(@RequestParam(value="name", defaultValue="newSession") String name,
                                    HttpSession session) {
        String ocEnv = (String) session.getAttribute("ocEnvironment");
        OcUser usr = getOcUser(ocEnv);
        UploadSession uploadSession = new UploadSession(name, UploadSession.Step.MAPPING, new Date(), usr);//TODO: Add remaining steps
        uploadSessionRepository.save(uploadSession);
    }

    private OcUser getOcUser(String ocEnv) { //TODO: Add tests
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); //get logged in username
        List<OcUser> byUsername = userRepository.findByUsername(username);
        List<OcUser> matching = byUsername.stream().filter(usr -> usr.getOcEnvironment() == ocEnv)
                .collect(Collectors.toList());
        if (matching.size() == 1) {
            return matching.get(0);
        } else if (matching.size() > 1) {
            log.error("More than one matching users sharing the same username and OcEnv: "+ matching.toString());
            return null;
        } else {
            log.error("Attempted retrieving non-existent user, OcUser not correctly saved to the database?");
            return null;
        }
    }
}
