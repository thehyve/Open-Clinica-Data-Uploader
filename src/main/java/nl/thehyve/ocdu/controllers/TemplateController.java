package nl.thehyve.ocdu.controllers;

import nl.thehyve.ocdu.factories.EventDataFactory;
import nl.thehyve.ocdu.factories.PatientDataFactory;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.services.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides file templates for the user.
 * Created by bo on 6/17/16.
 */
@Controller
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    OcUserService ocUserService;

    @Autowired
    UploadSessionService uploadSessionService;

    @Autowired
    DataService dataService;

    @Autowired
    OpenClinicaService openClinicaService;

    @Autowired
    ClinicalDataRepository clinicalDataRepository;


    @RequestMapping(value = "/get-subject-template", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getSubjectTemplate(@RequestParam("registerSite") boolean registerSite, HttpSession session) {
        try {
            UploadSession uploadSession = uploadSessionService.getCurrentUploadSession(session);
            OcUser user = ocUserService.getCurrentOcUser(session);
            String username = user.getUsername();
            String pwdHash = ocUserService.getOcwsHash(session);
            String url = user.getOcEnvironment();
            List<ClinicalData> clinicalDatas = clinicalDataRepository.findBySubmission(uploadSession);

            //key: subject id from user - val: technical subject id
            Map<String, String> subjectMap = openClinicaService.createMapSubjectLabelToSubjectOID(username, pwdHash, url, clinicalDatas);

            Study study = dataService.findStudy(uploadSession.getStudy(), user, pwdHash);
            MetaData metadata = openClinicaService.getMetadata(username, pwdHash, user.getOcEnvironment(), study);
            PatientDataFactory pdf = new PatientDataFactory(user, uploadSession);
            List<String> result = pdf.generatePatientRegistrationTemplate(metadata, subjectMap, registerSite);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/get-event-template", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getEventTemplate(HttpSession session) {
        try {
            UploadSession uploadSession = uploadSessionService.getCurrentUploadSession(session);
            OcUser user = ocUserService.getCurrentOcUser(session);
            String username = user.getUsername();
            String pwdHash = ocUserService.getOcwsHash(session);
            String url = user.getOcEnvironment();
            Study study = dataService.findStudy(uploadSession.getStudy(), user, pwdHash);
            MetaData metadata = openClinicaService.getMetadata(username, pwdHash, user.getOcEnvironment(), study);
            EventDataFactory edf = new EventDataFactory(user, uploadSession);
            Set<ImmutablePair> patientsInEvent = dataService.getPatientsInEvent(uploadSession);
            List<StudySubjectWithEventsType> subjectWithEventsTypes = openClinicaService
                    .getStudySubjectsType(username, pwdHash, url, study.getIdentifier(), "");

            List<String> result = edf.generateEventSchedulingTemplate(metadata, subjectWithEventsTypes, patientsInEvent);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
