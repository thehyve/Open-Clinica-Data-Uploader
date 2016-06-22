package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.StudyDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.EventRepository;
import nl.thehyve.ocdu.repositories.SubjectRepository;
import nl.thehyve.ocdu.validators.ClinicalDataChecksRunner;
import nl.thehyve.ocdu.validators.ClinicalDataOcChecks;
import nl.thehyve.ocdu.validators.EventDataOcChecks;
import nl.thehyve.ocdu.validators.PatientDataOcChecks;
import nl.thehyve.ocdu.validators.fileValidators.DataPreMappingValidator;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
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

    @Autowired
    DataService dataService;


    public List<ValidationErrorMessage> getDataErrors(UploadSession submission, String wsPwdHash) throws Exception {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        determineStudy(bySubmission, submission);
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);
        MetaData metadata = openClinicaService
                .getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        List<StudySubjectWithEventsType> subjectWithEventsTypes = openClinicaService
                .getStudySubjectsType(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        List<ValidationErrorMessage> errors = new ArrayList<>();
        if (study == null || metadata == null) {
            StudyDoesNotExist studyError = new StudyDoesNotExist();
            studyError.addOffendingValue(submission.getStudy());
            errors.add(studyError);
        } else {
            ClinicalDataChecksRunner checksRunner = new ClinicalDataOcChecks(metadata, bySubmission, subjectWithEventsTypes);
            errors.addAll(checksRunner.getErrors());
        }
        return errors;
    }

    public List<ValidationErrorMessage> getEventsErrors(UploadSession submission, String wsPwdHash) throws Exception {
        List<Event> events = eventRepository.findBySubmission(submission);
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);
        MetaData metadata = openClinicaService
                .getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        EventDataOcChecks checks = new EventDataOcChecks(metadata, events);
        List<ValidationErrorMessage> validationErrorMessages = checks.getErrors();
        return validationErrorMessages;
    }

    public List<ValidationErrorMessage> getPatientsErrors(UploadSession submission, String wsPwdHash) throws Exception {
        List<Subject> bySubmission = subjectRepository.findBySubmission(submission);
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);
        MetaData metadata = openClinicaService.getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        List<ValidationErrorMessage> errors = new ArrayList<>();

        PatientDataOcChecks checksRunner = new PatientDataOcChecks(metadata, bySubmission);
        errors.addAll(checksRunner.getErrors());
        return errors;
    }

    public List<ValidationErrorMessage> getFinallErrors(UploadSession submission) {
        ArrayList<ValidationErrorMessage> validationErrorMessages = new ArrayList<>();
        //TODO: implement generating validation error messages
        return validationErrorMessages;
    }

    private void determineStudy(Collection<ClinicalData> entries, UploadSession submission) {
        Set<String> usedStudyOIDs = entries.stream().map(ocEntity -> ocEntity.getStudy()).collect(Collectors.toSet());
        if (usedStudyOIDs.size() > 1) log.error("Attempted validation of file referencing multiple studies");
        submission.setStudy(usedStudyOIDs.stream().findFirst().get()); // Multiple studies not allowed, checked by a validator
    }

    public Collection<ValidationErrorMessage> dataPremappingValidation(UploadSession submission, String wsPwdHash) throws Exception {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        determineStudy(bySubmission, submission);
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);
        MetaData metadata = openClinicaService
                .getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        List<StudySubjectWithEventsType> subjectWithEventsTypes = openClinicaService
                .getStudySubjectsType(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        List<ValidationErrorMessage> errors = new ArrayList<>();

        ClinicalDataChecksRunner checksRunner = new DataPreMappingValidator(metadata, bySubmission, subjectWithEventsTypes);
        errors.addAll(checksRunner.getErrors());
        return errors;
    }
}
