package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OCEntities.Site;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.RegisteredEventInformation;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.IsStudySubjectResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListAllByStudyResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListStudiesResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.OCResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.SOAPResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils;
import nl.thehyve.ocdu.soap.SOAPRequestFactory;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.EventResponseType;
import org.openclinica.ws.beans.EventType;
import org.openclinica.ws.beans.StudySubjectRefType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.*;
import java.util.stream.Collectors;

import static nl.thehyve.ocdu.soap.ResponseHandlers.RegisterSubjectsResponseHandler.parseRegisterSubjectsResponse;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */

@Service
public class OpenClinicaService {

    @Autowired
    ODMService odmService;

    SOAPRequestFactory requestFactory = new SOAPRequestFactory();
    private static final Logger log = LoggerFactory.getLogger(OpenClinicaService.class);


    public String registerPatients(String username, String passwordHash, String url, Collection<Subject> subjects)
            throws Exception {
        log.info("Register patients initialized by: " + username + " on: " + url);
        SOAPMessage soapMessage = requestFactory.createCreateSubject(username, passwordHash, subjects);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage soapResponse = soapConnection.call(soapMessage, url + "/ws/studySubject/v1");
        String error = parseRegisterSubjectsResponse(soapResponse);
        if (error != null) {
            String detailedErrorMessage = "Registering subjects against instance " + url + " failed, OC error: " + error;
            log.error(detailedErrorMessage);
            return detailedErrorMessage;
        } else {
            log.info("Registering subjects against instance " + url + " successfull, number of subjects:" +
                    subjects.size());
            return "";
        }
    }

    public List<Study> listStudies(String username, String passwordHash, String url) throws Exception { //TODO: handle exceptions
        log.info("List studies initiated by: " + username + " on: " + url);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = requestFactory.createListStudiesRequest(username, passwordHash);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/study/v1");  // Add SOAP endopint to OCWS URL.
        List<Study> studies = ListStudiesResponseHandler.parseListStudiesResponse(soapResponse);
        soapConnection.close();
        return studies;
    }

    public Map<String, String> createMapSubjectLabelToSubjectOID(String username,
                                                                 String passwordHash,
                                                                 String url,
                                                                 List<ClinicalData> clinicalDataList) throws Exception {
        // TODO this mapping can be made redundant if the subjectOID is also returned by the listAllByStudy
        // call. In this way you avoid N-calls toe isStudySubject for N-subjects.
        // We assume that all subject in the clinicalData-list are registered.
        Map<String, String> ret = new HashMap<>(clinicalDataList.size());
        for (ClinicalData clinicalData : clinicalDataList) {
            String subjectLabel = clinicalData.getSsid();
            String studyLabel = clinicalData.getStudy();
            String subjectOID = getSubjectOID(username, passwordHash, url, studyLabel, subjectLabel);
            ret.put(subjectLabel, subjectOID);
        }
        return ret;
    }


    public MetaData getMetadata(String username, String passwordHash, String url, Study study) throws Exception {
        log.info("Get metadata initiated by: " + username + " on: " + url + " study: " + study);
        if (study == null || username == null || passwordHash == null || url == null) {
            return null;
        }
        MetaData metaData = getMetadataSoapCall(username, passwordHash, url, study);
        addSiteDefinitions(metaData, username, passwordHash, url, study);
        addSiteInformationToMetaData(metaData, study);
        return metaData;
    }

    private MetaData getMetadataSoapCall(String username, String passwordHash, String url, Study study) throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = requestFactory.createGetStudyMetadataRequest(username, passwordHash, study);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/study/v1");  // Add SOAP endopint to OCWS URL.
        MetaData metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(soapResponse);
        soapConnection.close();
        return metaData;
    }

    public List<String> uploadClinicalData(String username,
                                           String passwordHash,
                                           String url,
                                           List<ClinicalData> clinicalDataList,
                                           MetaData metaData,
                                           String statusAfterUpload,
                                           Map<String, String> subjectLabelToOIDMap) throws Exception {
        log.info("Upload initiated by: " + username + " on: " + url);
        List<String> resultList = new ArrayList();

        if (StringUtils.isEmpty(username) ||
                StringUtils.isEmpty(passwordHash) ||
                StringUtils.isEmpty(url)) {
            resultList.add("One of the required parameters is missing (username, password or URL)");
            return resultList;
        }

        Map<String, List<ClinicalData>> outputMap = clinicalDataList.stream().collect(Collectors.groupingBy(ClinicalData::createODMGroupingKey,
                Collectors.toList()));
        TreeMap<String, List<ClinicalData>> sortedMap = new TreeMap<>(outputMap);
        for (String key : sortedMap.keySet()) {
            List<ClinicalData> outputClinicalData = sortedMap.get(key);
            String odmString = odmService.generateODM(outputClinicalData, metaData, statusAfterUpload, subjectLabelToOIDMap);
            String uploadResult = uploadODMString(username, passwordHash, url, odmString);
            if (uploadResult == null) {
                resultList.add("Successfully uploaded data for subject " + key);
            } else {
                resultList.add("Failed upload for subject " + key + ". Cause: " + uploadResult);
            }
        }
        return resultList;
    }
    private void addSiteDefinitions(MetaData metaData, String username, String passwordHash, String url, Study study) throws Exception {
        List<SiteDefinition> siteDefs = new ArrayList<>();
        for(Site site: study.getSiteList()) {
            Study siteAsAStudy = new Study(site.getIdentifier(), site.getOid(), site.getName());
            MetaData siteMetadata = getMetadataSoapCall(username, passwordHash, url, siteAsAStudy);
            SiteDefinition siteDef = new SiteDefinition();
            siteDef.setSiteOID(site.getOid());
            siteDef.setName(site.getName());
            siteDef.setUniqueID(site.getIdentifier());
            siteDef.setBirthdateRequired(siteMetadata.getBirthdateRequired());
            siteDef.setGenderRequired(siteMetadata.isGenderRequired());
            siteDefs.add(siteDef);
        }
        metaData.setSiteDefinitions(siteDefs);
    }

    /**
     * @param username     the user-account name
     * @param passwordHash the SHA1 hash of the user's password
     * @param url          the URL of the OpenClinica-ws instance
     * @param odm          the ODM string to upload
     * @return a non <code>null</code> error code.message if an error occurred. Some are reported by the OpenClinica-WS
     * instance at url. Returns <code>null</code> if everything went OK.
     * @throws Exception in case of a technical error
     */
    private String uploadODMString(String username, String passwordHash, String url, String odm) throws Exception {


        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage soapMessage = requestFactory.createDataUploadRequest(username, passwordHash, odm);

        SOAPMessage soapResponse = soapConnection.call(soapMessage, url + "/ws/data/v1");  // Add SOAP endopint to OCWS URL.
        String responseError = SOAPResponseHandler.parseOpenClinicaResponse(soapResponse, "//importDataResponse");
        if (responseError != null) {
            log.error("ImportData request failed: " + responseError);
        }
        return responseError;
    }

    /**
     * Schedule all the events found in {@param eventList} but which have not been scheduled yet in
     * OpenClinica according to the information present in the {@param studyEventDefinitionTypeList}.
     * @param username
     * @param passwordHash
     * @param url
     * @param eventList
     * @throws Exception
     */
    public String scheduleEvents(String username, String passwordHash, String url,
                                 MetaData metaData,
                               List<Event> eventList,
                               List<StudySubjectWithEventsType> studySubjectWithEventsTypeList) throws Exception {
        log.info("Schedule events initiated by: " + username + " on: " + url);
        if (StringUtils.isEmpty(username) ||
                StringUtils.isEmpty(passwordHash) ||
                StringUtils.isEmpty(url)) {
            return "One of the required parameters is missing (username, password, url)";
        }
        Map<String, String> eventNameOIDMap =
                metaData.getEventDefinitions().stream().collect(Collectors.toMap(EventDefinition::getName, EventDefinition::getStudyEventOID));

        Map<String, EventResponseType> eventsRegisteredInOpenClinica =
                RegisteredEventInformation.createEventKeyList(studySubjectWithEventsTypeList);
        List<EventType> eventTypeList = new ArrayList<>();
        for (Event event : eventList) {
            String eventOID = eventNameOIDMap.get(event.getEventName());
            String eventKey = event.createEventKey(eventOID);
            if ( ! eventsRegisteredInOpenClinica.containsKey(eventKey)) {
                EventType eventType = event.createEventType(eventNameOIDMap);
                StudySubjectRefType studySubjectRefType = new StudySubjectRefType();
                studySubjectRefType.setLabel(event.getSsid());
                eventType.setStudySubjectRef(studySubjectRefType);

                if (StringUtils.isEmpty(eventOID)) {
                    throw new IllegalStateException("No eventName specified in the input for subject " + event.getSsid());
                }
                eventType.setEventDefinitionOID(eventOID);
                // TODO remove these hardcoded values and obtain them from the BusinessLogic bean still to be
                // created
                eventType.setLocation("Utrecht");
                XMLGregorianCalendar startDate = SoapUtils.getFullXmlDate((GregorianCalendar) GregorianCalendar.getInstance());
                eventType.setStartDate(startDate);
                eventTypeList.add(eventType);
            }
        }

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        StringBuffer errorMessage = new StringBuffer();
        for (EventType eventType : eventTypeList) {
            SOAPMessage soapMessage = requestFactory.createScheduleEventRequest(username, passwordHash, eventType);
            System.out.println("SOAP:----->\n" + SoapUtils.soapMessageToString(soapMessage));
            SOAPMessage soapResponse = soapConnection.call(soapMessage, url + "/ws/event/v1");
            String responseError = SOAPResponseHandler.parseOpenClinicaResponse(soapResponse, "//scheduleResponse");
            if (responseError != null) {
                log.error("ScheduleEvent request failed: " + responseError);
                errorMessage.append(responseError);
            }
        }
        return errorMessage.toString();
    }


    public List<StudySubjectWithEventsType> getStudySubjectsType(String username, String passwordHash, String url, String studyIdentifier, String siteIdentifier) throws Exception {
        log.info("Get listAllByStudy by: " + username + " on: " + url + " study: " + siteIdentifier + " site: " + siteIdentifier);
        if (studyIdentifier == null || username == null || passwordHash == null || url == null) {
            return null;
        }
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage soapMessage = requestFactory.createListAllByStudy(username, passwordHash, studyIdentifier, siteIdentifier);
        SOAPMessage soapResponse = soapConnection.call(soapMessage, url + "/ws/studySubject/v1");  // Add SOAP endopint to OCWS URL.
        List<StudySubjectWithEventsType> subjectsTypeList =
                ListAllByStudyResponseHandler.retrieveStudySubjectsType(soapResponse);

        soapConnection.close();
        return subjectsTypeList;
    }


    public boolean isAuthenticated(String username, String /* hexdigest of sha1 password */ passwordHash, String url) throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = requestFactory.createListStudiesRequest(username, passwordHash);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/study/v1");  // Add SOAP endopint to OCWS URL.
        Document responseXml = SoapUtils.toDocument(soapResponse);
        soapConnection.close();
        return StringUtils.isEmpty(OCResponseHandler.isAuthFailure(responseXml));
    }

    /**
     * Retrieves the corresponding OpenClinica studySubjectOID of a <code>subjectLabel</code>with a SOAP-call to the
     * OpenClinica instance at <code>url</code>.
     *
     * @param username     the user name
     * @param passwordHash the SHA1 hashed password
     * @param url          the url to the OpenClinica-WS instance
     * @param studyLabel   the study label
     * @param subjectLabel the subject label
     * @return <code>null</code> if the subjectLabel does not exist in the study.
     * @throws Exception in case of problems
     */
    private String getSubjectOID(String username, String passwordHash, String url, String studyLabel, String subjectLabel) throws Exception {
        log.info("Get isStudySubject initiated by: " + username + " on: " + url + " study: " + studyLabel);
        if (studyLabel == null || username == null || passwordHash == null || url == null || subjectLabel == null) {
            return null;
        }
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message =
                requestFactory.createIsStudySubjectRequest(username, passwordHash, studyLabel, subjectLabel);

        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/studySubject/v1");  // Add SOAP endopint to OCWS URL.
        String studySubjectOID = IsStudySubjectResponseHandler.parseIsStudySubjectResponse(soapResponse);

        return studySubjectOID;
    }

    private void addSiteInformationToMetaData(MetaData metaData, Study study) {
        // TODO change outer loop to Lambda
        for (SiteDefinition siteDefinition : metaData.getSiteDefinitions()) {
            String searchOID = siteDefinition.getSiteOID();
            List<Site> searchSiteList =
                    study.getSiteList().stream().filter(site -> site.getOid().equals(searchOID)).collect(Collectors.toList());
            if (! searchSiteList.isEmpty()) {
                Site searchSite = searchSiteList.get(0);
                siteDefinition.setUniqueID(searchSite.getIdentifier());
            }
        }
    }
}
