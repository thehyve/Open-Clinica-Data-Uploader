package nl.thehyve.ocdu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by piotrzakrzewski on 22/03/16.
 */
@Controller
public class LoginController {

    @RequestMapping(value="/ind", method= RequestMethod.GET)
    public String ind() {
        return "login";
    }

}
