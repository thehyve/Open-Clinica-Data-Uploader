package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.ClinicalData;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.EventRepository;
import nl.thehyve.ocdu.repositories.SubjectRepository;
import nl.thehyve.ocdu.validators.DataFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */

@Service
public class FileService {

    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SubjectRepository subjectRepository;

    public List<String> depositDataFile(File dataFile, String username, String submissionName) {
        DataFileValidator validator = new DataFileValidator();
        validator.validateFile(dataFile);
        List<String> errorMsgs = new ArrayList<>();
        if (validator.isValid()) {
            List<ClinicalData> newEntries = null;
            clinicalDataRepository.save(newEntries);
            return errorMsgs;
        } else {
            errorMsgs = validator.getErrorMessages();
            return errorMsgs;
        }
    }



}
