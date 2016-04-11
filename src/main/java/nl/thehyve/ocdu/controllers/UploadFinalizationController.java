package nl.thehyve.ocdu.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class UploadFinalizationController {

    @RequestMapping(value="/final", method= RequestMethod.GET)
    public String login() {
        return "final";
    }

}
