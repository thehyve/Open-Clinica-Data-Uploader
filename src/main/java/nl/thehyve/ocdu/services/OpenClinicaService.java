package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.IsStudySubjectResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListAllByStudyResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListStudiesResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.OCResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils;
import nl.thehyve.ocdu.soap.SOAPRequestFactory;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */

@Service
public class OpenClinicaService {

    SOAPRequestFactory responseFactory = new SOAPRequestFactory();
    private static final Logger log = LoggerFactory.getLogger(OpenClinicaService.class);

    public List<Study> listStudies(String username, String passwordHash, String url) throws Exception { //TODO: handle exceptions
        log.info("List studies initiated by: " + username + " on: " + url);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = responseFactory.createListStudiesRequest(username, passwordHash);
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
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = responseFactory.createGetStudyMetadataRequest(username, passwordHash, study);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/study/v1");  // Add SOAP endopint to OCWS URL.
        MetaData metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(soapResponse);
        soapConnection.close();
        return metaData;
    }


    public List<StudySubjectWithEventsType> getStudySubjectsType(String username, String passwordHash, String url, Study study) throws Exception {
        log.info("Get listAllByStudy by: " + username + " on: " + url + " study: " + study);
        if (study == null || username == null || passwordHash == null || url == null) {
            return null;
        }
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage soapMessage = responseFactory.createListAllByStudy(username, passwordHash, study);
        SOAPMessage soapResponse = soapConnection.call(soapMessage, url + "/ws/studySubject/v1");  // Add SOAP endopint to OCWS URL.
        List<StudySubjectWithEventsType> subjectsTypeList =
                ListAllByStudyResponseHandler.retrieveStudySubjectsType(soapResponse);

        soapConnection.close();
        return subjectsTypeList;
    }


    public boolean isAuthenticated(String username, String /* hexdigest of sha1 password */ passwordHash, String url) throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = responseFactory.createListStudiesRequest(username, passwordHash);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/study/v1");  // Add SOAP endopint to OCWS URL.
        Document responseXml = SoapUtils.toDocument(soapResponse);
        soapConnection.close();
        return !OCResponseHandler.isAuthFailure(responseXml);
    }

    /**
     * Retrieves the corresponding OpenClinica studySubjectOID of a <code>subjectLabel</code>with a SOAP-call to the
     * OpenClinica instance at <code>url</code>.
     * @param username the user name
     * @param passwordHash the SHA1 hashed password
     * @param url the url to the OpenClinica-WS instance
     * @param studyLabel the study label
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
        SOAPMessage message = responseFactory.createIsStudySubjectRequest(username, passwordHash, studyLabel, subjectLabel);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/studySubject/v1");  // Add SOAP endopint to OCWS URL.
        String studySubjectOID = IsStudySubjectResponseHandler.parseIsStudySubjectResponse(soapResponse);

        return studySubjectOID;
    }
}
