package nl.thehyve.ocdu.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bo on 4/15/16.
 */

@Controller
public class MappingController {

    @RequestMapping(value="/mapping", method= RequestMethod.GET)
    public String mapping() {
        return "mapping";
    }

}
