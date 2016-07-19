package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.EventRepository;
import nl.thehyve.ocdu.repositories.SubjectRepository;
import nl.thehyve.ocdu.services.DataService;
import nl.thehyve.ocdu.services.OcUserService;
import nl.thehyve.ocdu.services.OpenClinicaService;
import nl.thehyve.ocdu.services.UploadSessionService;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for the upload of ODM-data to OpenClinica.
 * Created by Jacob Rousseau on 28-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
@Controller
@RequestMapping("/odm")
public class ODMUploadController {

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    OcUserService ocUserService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    OpenClinicaService openClinicaService;

    @Autowired
    DataService dataService;

    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Collection<ValidationErrorMessage>> uploadODM(HttpSession session) {
        Collection<ValidationErrorMessage> result = new ArrayList<>();
        try {
            UploadSession uploadSession = uploadSessionService.getCurrentUploadSession(session);

            OcUser user = ocUserService.getCurrentOcUser(session);
            String userName = user.getUsername();
            String pwdHash = ocUserService.getOcwsHash(session);
            String url = user.getOcEnvironment();

            Study study = dataService.findStudy(uploadSession.getStudy(), user, pwdHash);
            MetaData metaData =
                    openClinicaService.getMetadata(userName, pwdHash, user.getOcEnvironment(), study);

            List<StudySubjectWithEventsType> studySubjectWithEventsTypeList =
                    openClinicaService.getStudySubjectsType(userName, pwdHash, url, study.getIdentifier(), "");

            Collection<Subject> subjects = subjectRepository.findBySubmission(uploadSession);
            Collection<ValidationErrorMessage> resultSubjectRegistration = new ArrayList<>();
            if (!subjects.isEmpty()) {
                resultSubjectRegistration = openClinicaService.registerPatients(userName, pwdHash, url, subjects);
            }
            if (resultSubjectRegistration.size() > 0) {
                resultSubjectRegistration.add(new ValidationErrorMessage("Event registration aborted due to errors in subject registration"));
            }
            List<Event> eventList = eventRepository.findBySubmission(uploadSession);
            Collection<ValidationErrorMessage> resultEventRegistration = new ArrayList<>();
            if (!eventList.isEmpty() && resultSubjectRegistration.size() == 0) {
                resultEventRegistration = openClinicaService.scheduleEvents(userName, pwdHash, url, metaData, eventList, studySubjectWithEventsTypeList);
            }
//            List<ClinicalData> clinicalDataList =
//                    clinicalDataRepository.findBySubmission(uploadSession);
//        result =
//                openClinicaService.uploadClinicalData(userName, pwdHash, url, clinicalDataList, metaData, uploadSession);
            result.addAll(resultSubjectRegistration);
            result.addAll(resultEventRegistration);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            ValidationErrorMessage errorMessage = new ValidationErrorMessage(e.getMessage());
            result.add(errorMessage);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
