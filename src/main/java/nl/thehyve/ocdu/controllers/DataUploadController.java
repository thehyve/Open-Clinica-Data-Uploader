package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.OCEnvironmentsConfig;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.services.FileService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.UploadSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
@Controller
public class DataUploadController {

    public static String TMPDIR = System.getProperty("java.io.tmpdir");

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String data() {
        return "data";
    }


    @Autowired
    FileService fileService;

    @Autowired
    OcUserService ocUserService;

    @Autowired
    UploadSessionService uploadSessionService;

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFile(
            @RequestParam("uploadfile") MultipartFile uploadfile, HttpSession session) {

        try {
            OcUser user = ocUserService.getCurrentOcUser(session);
            Path locallySavedDataFile = saveFile(uploadfile);
            UploadSession currentUploadSession = uploadSessionService.getCurrentUploadSession(session);
            fileService.depositDataFile(locallySavedDataFile, user, currentUploadSession );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    } // method uploadFile

    @RequestMapping(value = "/uploadMapping", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadMapping(
            @RequestParam("uploadmapping") MultipartFile uploadmapping) {
        System.out.println("mapping mapping");
        try {
            saveFile(uploadmapping);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Path saveFile(MultipartFile file) throws IOException {
        // Get the filename and build the local file path
        String filename = file.getOriginalFilename();
        String directory = System.getProperty("java.io.tmpdir");
        String filepath = Paths.get(directory, filename).toString();

        // Save the file locally
        BufferedOutputStream stream =
                new BufferedOutputStream(new FileOutputStream(new File(filepath)));
        stream.write(file.getBytes());
        stream.close();
        return Paths.get(filepath);
    }

}
