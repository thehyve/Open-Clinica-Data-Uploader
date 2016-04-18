package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.Study;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListStudiesResponseHandler;
import nl.thehyve.ocdu.soap.SOAPRequestFactory;
import org.springframework.stereotype.Service;

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

    public  List<Study> listStudies(String username, String passwordHash, String url) throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage message = responseFactory.createListStudiesRequest(username, passwordHash);
        SOAPMessage soapResponse = soapConnection.call(message, url);
        List<Study> studies =  ListStudiesResponseHandler.parseListStudiesResponse(soapResponse);
        soapConnection.close();
        return studies;
    }

}
