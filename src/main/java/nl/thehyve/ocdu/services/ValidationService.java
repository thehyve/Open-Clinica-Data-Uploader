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
 * Service performing OpenClinica consistency checking. Supports checking data, subjects and events for
 * consistency against targeted study/site. All methods here depend on OpenClinica Web-Services and therefore
 * require valid OcEnvironment (reachable and working OC 3.6 Server with SOAP-ws installed and configured)
 * and need valid OC User and sha1 hash of their password. All methods of this service accept UploadSession
 * (aka User Submission) as input - this object represents submission user made along with all submitted data.
 * This object is used to retrieve saved data/subjects/events and validate them.
 *
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

    /**
     * Returns errors in consistency against OpenClinica study definition (metadata) in user submitted data.
     *
     * @param submission
     * @param wsPwdHash
     * @return
     * @throws Exception
     */
    public List<ValidationErrorMessage> getDataErrors(UploadSession submission, String wsPwdHash) throws Exception {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        determineStudy(bySubmission, submission);
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);

        MetaData metadata = openClinicaService
                .getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        List<StudySubjectWithEventsType> subjectWithEventsTypes = openClinicaService
                .getStudySubjectsType(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study.getIdentifier(), "");
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

    /**
     * Returns errors in consistency against OpenClinica study definition (metadata) in event registration form
     * subitted by the user.
     *
     * @param submission
     * @param wsPwdHash
     * @return
     * @throws Exception
     */
    public List<ValidationErrorMessage> getEventsErrors(UploadSession submission, String wsPwdHash) throws Exception {
        List<Event> events = eventRepository.findBySubmission(submission);
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);
        MetaData metadata = openClinicaService
                .getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        events.forEach(event -> event.setStudyProtocolName(metadata.getProtocolName())); //TODO: Refactor setting studyProtocolName out of validation , this is not the right place to do it
        eventRepository.save(events);
        EventDataOcChecks checks = new EventDataOcChecks(metadata, events);
        List<ValidationErrorMessage> validationErrorMessages = checks.getErrors();
        return validationErrorMessages;
    }

    /**
     *
     * Returns errors in consistency against OpenClinica study definition (metadata) in user submitted subject
     * registration form.
     *
     * @param submission
     * @param wsPwdHash
     * @return
     * @throws Exception
     */
    public List<ValidationErrorMessage> getPatientsErrors(UploadSession submission, String wsPwdHash) throws Exception {
        List<Subject> bySubmission = subjectRepository.findBySubmission(submission);
        Set<String> subjectsInData = clinicalDataRepository.findBySubmission(submission)
                .stream().map(ClinicalData::getSsid).collect(Collectors.toSet());
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);
        MetaData metadata = openClinicaService.getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        bySubmission.forEach(subject -> subject.setStudyProtocolName(metadata.getProtocolName())); //TODO: Refactor setting studyOID out of validation , this is not the right place to do it
        subjectRepository.save(bySubmission);
        List<StudySubjectWithEventsType> subjectWithEventsTypes = openClinicaService
                .getStudySubjectsType(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study.getIdentifier(), "");

        List<ValidationErrorMessage> errors = new ArrayList<>();
        PatientDataOcChecks checksRunner = new PatientDataOcChecks(metadata, bySubmission, subjectWithEventsTypes, subjectsInData);
        errors.addAll(checksRunner.getErrors());
        return errors;
    }

    public List<ValidationErrorMessage> getFinallErrors(UploadSession submission) {
        ArrayList<ValidationErrorMessage> validationErrorMessages = new ArrayList<>();
        //TODO: implement generating validation error messages
        return validationErrorMessages;
    }

    /**
     * Sets study field in UploadSubmission, inferring from the data submitted by the user.
     * Please mind that UploadSession is not saved - this method is called
     * on every validation run - to account for possible changes in user data (if for instance, resubmitting was
     * possible)
     * @param entries
     * @param submission
     */
    private void determineStudy(Collection<ClinicalData> entries, UploadSession submission) {
        Set<String> usedStudyOIDs = entries.stream().map(ocEntity -> ocEntity.getStudy()).collect(Collectors.toSet());
        if (usedStudyOIDs.size() > 1) log.error("Attempted validation of file referencing multiple studies");
        submission.setStudy(usedStudyOIDs.stream().findFirst().get()); // Multiple studies not allowed, checked by a validator
    }

    /**
     * Responsible for finding errors in the data that would prevent displaying mapping view.
     * This method does not check however for data format errors - those are checked by FileValidator.
     *
     * @param submission
     * @param wsPwdHash
     * @return
     * @throws Exception
     */
    public Collection<ValidationErrorMessage> dataPremappingValidation(UploadSession submission, String wsPwdHash) throws Exception {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        determineStudy(bySubmission, submission);
        OcUser submitter = submission.getOwner();
        Study study = dataService.findStudy(submission.getStudy(), submitter, wsPwdHash);
        MetaData metadata = openClinicaService
                .getMetadata(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study);
        List<StudySubjectWithEventsType> subjectWithEventsTypes = openClinicaService
                .getStudySubjectsType(submitter.getUsername(), wsPwdHash, submitter.getOcEnvironment(), study.getIdentifier(), "");
        List<ValidationErrorMessage> errors = new ArrayList<>();

        ClinicalDataChecksRunner checksRunner = new DataPreMappingValidator(metadata, bySubmission, subjectWithEventsTypes);
        errors.addAll(checksRunner.getErrors());
        return errors;
    }
}
