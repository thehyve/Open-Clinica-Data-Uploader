package nl.thehyve.ocdu.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by piotrzakrzewski on 22/03/16.
 */

@Controller
public class ViewsControllerController {

    private static final Logger log = LoggerFactory.getLogger(ViewsControllerController.class);

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/final", method = RequestMethod.GET)
    public String finalView() {
        return "final";
    }

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    public String patients() {
        return "patients";
    }

    @RequestMapping(value = "/mapping", method = RequestMethod.GET)
    public String mapping() {
        return "mapping";
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public String events() {
        return "events";
    }

}
