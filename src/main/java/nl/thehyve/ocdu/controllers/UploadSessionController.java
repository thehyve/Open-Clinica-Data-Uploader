package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.UploadSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by bo on 4/26/16.
 */
@RestController
public class UploadSessionController {

    @RequestMapping(value="/username", method= RequestMethod.GET)
    public String username() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "???";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    @RequestMapping(value="/unfinished-sessions", method= RequestMethod.GET)
    public List<UploadSession> unfinishedSessions() {

        List<UploadSession> sessions = new ArrayList<UploadSession>();


        return sessions;
    }
}
