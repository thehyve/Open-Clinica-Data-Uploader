package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.OcEntity;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.EventRepository;
import nl.thehyve.ocdu.repositories.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Service
public class ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    OpenClinicaService openClinicaService;

    public List<ValidationErrorMessage> getDataErrors(UploadSession submission, String wsPwdHash) throws Exception {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        determineStudy(bySubmission, submission);
        OcUser submitter = submission.getOwner();
        ArrayList<ValidationErrorMessage> validationErrorMessages = new ArrayList<>();
        Study study = new Study("",submission.getStudy(),"");
        openClinicaService.getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        return validationErrorMessages;
    }

    public List<ValidationErrorMessage> getEventsErrors(UploadSession submission) {
        ArrayList<ValidationErrorMessage> validationErrorMessages = new ArrayList<>();
        //TODO: implement generating validation error messages
        return validationErrorMessages;
    }

    public List<ValidationErrorMessage> getPatientsErrors(UploadSession submission) {
        ArrayList<ValidationErrorMessage> validationErrorMessages = new ArrayList<>();
        //TODO: implement generating validation error messages
        return validationErrorMessages;
    }

    public List<ValidationErrorMessage> getFinallErrors(UploadSession submission) {
        ArrayList<ValidationErrorMessage> validationErrorMessages = new ArrayList<>();
        //TODO: implement generating validation error messages
        return validationErrorMessages;
    }

    private void determineStudy(Collection<ClinicalData> entries, UploadSession submission) {
        Set<String> usedStudyOIDs = entries.stream().map(ocEntity -> ocEntity.getStudy()).collect(Collectors.toSet());
        if (usedStudyOIDs.size() > 1) log.error("Attempted validation of file referencing multiple studies");
        submission.setStudy(usedStudyOIDs.stream().findFirst().get());
    }

}
