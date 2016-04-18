package nl.thehyve.ocdu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by piotrzakrzewski on 22/03/16.
 */
@Controller
public class LoginController {

    @RequestMapping(value="/login", method= RequestMethod.GET)
    public String login() {
        return "login";
    }


}
