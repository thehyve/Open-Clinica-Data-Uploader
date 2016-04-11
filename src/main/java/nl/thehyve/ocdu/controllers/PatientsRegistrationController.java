package nl.thehyve.ocdu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
@Controller
public class PatientsRegistrationController {

    @RequestMapping(value="/patients", method= RequestMethod.GET)
    public String patients() {
        return "patients";
    }

}
