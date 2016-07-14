package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OcItemMapping;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.services.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
@Controller
@RequestMapping("/upload")
public class UploadController {


    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    FileService fileService;

    @Autowired
    OcUserService ocUserService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    MappingService mappingService;

    @Autowired
    ValidationService validationService;

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Collection<ValidationErrorMessage>> uploadFile(
            @RequestParam("uploadfile") MultipartFile uploadfile, HttpSession session) {

        try {
            OcUser user = ocUserService.getCurrentOcUser(session);
            Path locallySavedDataFile = saveFile(uploadfile);
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            String pwd = ocUserService.getOcwsHash(session);
            Collection<ValidationErrorMessage> fileFormatErrors = fileService.depositDataFile(locallySavedDataFile, user, currentUploadSession, pwd);
            Collection<ValidationErrorMessage> allErrors = new ArrayList<>();
            if (fileFormatErrors.size() == 0) {
                Collection<ValidationErrorMessage> mappingPreventingErrors =
                        validationService.dataPremappingValidation(currentUploadSession, pwd);
                allErrors.addAll(mappingPreventingErrors);
            }
            allErrors.addAll(fileFormatErrors);
            return new ResponseEntity<>(allErrors, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Collection<ValidationErrorMessage>> uploadEventsDataFile(
            @RequestParam("uploadEventFile") MultipartFile uploadfile, HttpSession session) {

        try {
            OcUser user = ocUserService.getCurrentOcUser(session);
            Path locallySavedDataFile = saveFile(uploadfile);
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            Collection<ValidationErrorMessage> fileFormatErrors = fileService
                    .depositEventsDataFile(locallySavedDataFile, user, currentUploadSession);
            return new ResponseEntity<>(fileFormatErrors, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    private Path saveFile(MultipartFile file) throws IOException {
        // Get the filename and build the local file path
        String filename = file.getOriginalFilename();
        String directory = System.getProperty("java.io.tmpdir");
        String filepath = Paths.get(directory, filename).toString();

        // Save the file locally
        BufferedOutputStream stream =
                new BufferedOutputStream(new FileOutputStream(new File(filepath)));
        BOMInputStream bis = new BOMInputStream(file.getInputStream(), false);
        IOUtils.copy(bis, stream);
        stream.close();
        bis.close();
        return Paths.get(filepath);
    }


    @RequestMapping(value = "/mapping", method = RequestMethod.POST)
    public ResponseEntity<List<OcItemMapping>> acceptMapping(HttpSession session, @RequestBody List<OcItemMapping> mappings) {
        if (!isValid(mappings)) {
            log.error("Incorrect mapping JSON provided.");
            return new ResponseEntity<>(mappings, HttpStatus.BAD_REQUEST);
        }
        try {
            UploadSession submission = uploadSessionService.getCurrentUploadSession(session);
            mappingService.applyMapping(mappings, submission);
            return new ResponseEntity<>(mappings, HttpStatus.OK);
        } catch (UploadSessionNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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

    @RequestMapping(value = "/subjects", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Collection<ValidationErrorMessage>> uploadPatientFile(
            @RequestParam("uploadPatientData") MultipartFile uploadPatientData, HttpSession session) {

        try {
            OcUser user = ocUserService.getCurrentOcUser(session);
            Path locallySavedDataFile = saveFile(uploadPatientData);
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            Collection<ValidationErrorMessage> fileFormatErrors = fileService.depositPatientFile(locallySavedDataFile, user, currentUploadSession);
            return new ResponseEntity<>(fileFormatErrors, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}
