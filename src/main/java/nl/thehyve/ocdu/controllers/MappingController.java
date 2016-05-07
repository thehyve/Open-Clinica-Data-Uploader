package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OcItemMapping;
import nl.thehyve.ocdu.models.UploadSession;
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

    @RequestMapping(value = "/upload-mapping", method = RequestMethod.POST)
    public ResponseEntity<List<OcItemMapping>> acceptMapping(HttpSession session, @RequestBody List<OcItemMapping> mappings) {
        if (!isValid(mappings)) {
            log.error("Incorrect mapping JSON provided.");
            return new ResponseEntity<>(mappings, HttpStatus.BAD_REQUEST);
        }
        UploadSession submission = uploadSessionService.getCurrentUploadSession(session);
        mappingService.applyMapping(mappings, submission);
        return new ResponseEntity<>(mappings, HttpStatus.OK);
    }

    private boolean isValid(List<OcItemMapping> mappings) {
        List<OcItemMapping> faulty = mappings.stream().filter(ocItemMapping -> {
            if (ocItemMapping.getCrfName() == null ||
                    ocItemMapping.getStudy() == null ||
                    ocItemMapping.getCrfVersion() == null ||
                    ocItemMapping.getEventName() == null ||
                    ocItemMapping.getOcItemName() == null ||
                    ocItemMapping.getUsrItemName() == null) return true;
            else return false;
        }).collect(Collectors.toList());
        if (faulty.size() > 0) {
            return false;
        } else return true;
    }
}


