package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OcItemMapping;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.services.DataService;
import nl.thehyve.ocdu.services.MappingService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.UploadSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 07/05/16.
 */
@RestController
public class MappingController {
    private static final Logger log = LoggerFactory.getLogger(MappingController.class);

    @Autowired
    MappingService mappingService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    DataService dataService;

    @RequestMapping(value = "/user-items")
    public ResponseEntity<List<String>> getUserItems(HttpSession session) {
        UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
        List<String> userItems = dataService.getUserItems(currentUploadSession);
        return new ResponseEntity<>(userItems,HttpStatus.OK);
    }


}


