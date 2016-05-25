package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.FileFormatError;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.EventRepository;
import nl.thehyve.ocdu.repositories.SubjectRepository;
import nl.thehyve.ocdu.validators.fileValidators.DataFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
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
    //TODO: Add events, subjects and clinical data to UploadSession so that they are destroyed when session is destroyed
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    OpenClinicaService openClinicaService;

    public List<FileFormatError> depositDataFile(Path dataFile, OcUser user, UploadSession submission, String pwd) throws Exception {
        List<Study> studies = openClinicaService.listStudies(user.getUsername(), pwd, user.getOcEnvironment());
        DataFileValidator validator = new DataFileValidator(studies);
        validator.validateFile(dataFile);
        List<FileFormatError> errorMsgs = new ArrayList<>();
        if (validator.isValid()) {
            ClinicalDataFactory factory = new ClinicalDataFactory(user, submission);
            List<ClinicalData> newEntries = factory.createClinicalData(dataFile);
            clinicalDataRepository.save(newEntries);
            return errorMsgs;
        } else {
            errorMsgs = validator.getErrorMessages();
            return errorMsgs;
        }
    }


}
