package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.services.DataService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.UploadSessionService;
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
public class MetadataController {

    @Autowired
    DataService dataService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    OcUserService ocUserService;

    @RequestMapping(value = "/data-info", method = RequestMethod.GET)
    public ResponseEntity<DataService.FieldsDetermined> getFieldsInfo(HttpSession session) {
        UploadSession submission = uploadSessionService.getCurrentUploadSession(session);
        DataService.FieldsDetermined info = dataService.getInfo(submission);
        if (info == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(info, OK);
        }
    }

    @RequestMapping(value = "/metadata-tree", method = RequestMethod.GET)
    public ResponseEntity<DataService.MetaDataTree> getMetadata(HttpSession session) {
        UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
        String pwd = ocUserService.getOcwsHash(session);
        try {
            DataService.MetaDataTree tree = dataService.getMetadataTree(currentUploadSession, pwd );
            return new ResponseEntity<>(tree, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
    }


}
