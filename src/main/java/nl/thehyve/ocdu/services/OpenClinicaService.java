package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListStudiesResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.OCResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils;
import nl.thehyve.ocdu.soap.SOAPRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */

@Service
public class OpenClinicaService {

    SOAPRequestFactory responseFactory = new SOAPRequestFactory();
    private static final Logger log = LoggerFactory.getLogger(OpenClinicaService.class);

    public List<Study> listStudies(String username, String passwordHash, String url) throws Exception { //TODO: handle exceptions
        log.info("List studies initiated by: "+username+" on: "+url);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = responseFactory.createListStudiesRequest(username, passwordHash);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/study/v1");  // Add SOAP endopint to OCWS URL.
        List<Study> studies = ListStudiesResponseHandler.parseListStudiesResponse(soapResponse);
        soapConnection.close();
        return studies;
    }

    public MetaData getMetadata(String username, String passwordHash, String url, Study study) throws Exception {
        log.info("Get metadata initiated by: "+username+" on: "+url+" study: "+study);
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

    public boolean isAuthenticated(String username, String /* hexdigest of sha1 password */ passwordHash, String url) throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = responseFactory.createListStudiesRequest(username, passwordHash);
        SOAPMessage soapResponse = soapConnection.call(message, url + "/ws/study/v1");  // Add SOAP endopint to OCWS URL.
        Document responseXml = SoapUtils.toDocument(soapResponse);
        soapConnection.close();
        return !OCResponseHandler.isAuthFailure(responseXml);
    }

}
