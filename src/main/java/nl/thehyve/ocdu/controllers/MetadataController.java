package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.MetaDataTree;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.services.DataService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.UploadSessionNotFoundException;
import nl.thehyve.ocdu.services.UploadSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static org.springframework.http.HttpStatus.OK;

/**
 * Created by piotrzakrzewski on 07/05/16.
 */
@RestController
@RequestMapping("/metadata")
public class MetadataController {

    private static final Logger log = LoggerFactory.getLogger(MetadataController.class);

    @Autowired
    DataService dataService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    OcUserService ocUserService;



    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public ResponseEntity<?> getMetadata(HttpSession session) {
        try {
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            String pwd = ocUserService.getOcwsHash(session);
            MetaDataTree tree = dataService.getMetadataTree(currentUploadSession, pwd );
            return new ResponseEntity<>(tree, HttpStatus.OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            String errorMessage = "no submission active";
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
