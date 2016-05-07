package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.services.DataService;
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

    public ResponseEntity<MetaData> getMetadata(HttpSession session) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED); //TODO implement returning metadata
       // return new ResponseEntity<>(metadata, HttpStatus.OK);
    }


}
