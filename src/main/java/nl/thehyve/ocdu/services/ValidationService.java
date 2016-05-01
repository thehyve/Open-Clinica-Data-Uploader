package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.ClinicalData;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.ValidationErrorMessage;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.EventRepository;
import nl.thehyve.ocdu.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Service
public class ValidationService {

    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SubjectRepository subjectRepository;

    public List<ValidationErrorMessage> getDataErrors(UploadSession submission) {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        ArrayList<ValidationErrorMessage> validationErrorMessages = new ArrayList<>();
        //TODO: implement generating validation error messages
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

}
