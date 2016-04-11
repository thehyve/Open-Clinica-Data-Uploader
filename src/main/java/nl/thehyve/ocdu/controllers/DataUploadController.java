package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
@Controller
public class DataUploadController {

    @RequestMapping(value="/data", method= RequestMethod.GET)
    public String data() {
        return "data";
    }

}
