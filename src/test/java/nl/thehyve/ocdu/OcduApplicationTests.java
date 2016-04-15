package nl.thehyve.ocdu;

import nl.thehyve.ocdu.models.Study;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListStudiesResponseHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.portable.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.soap.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class OcduApplicationTests {

    SOAPMessage mockedResponseListAllStudies;

    @Before
    public void setUp() {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapFactory = SOAPFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            SOAPEnvelope envelope = soapPart.getEnvelope();
            decorateBody(envelope);
            soapMessage.saveChanges();
            mockedResponseListAllStudies = soapMessage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void decorateBody(SOAPEnvelope envelope) {
        try {
            SOAPBody soapBody = envelope.getBody();
            SOAPElement listAllResponseEl = soapBody.addChildElement("listAllResponse");
            SOAPElement studiesEl = listAllResponseEl.addChildElement("studies");
            SOAPElement s1 = mockStudyEl("id1","oid1","name1");
            SOAPElement s2 = mockStudyEl("id2","oid2","name2");
            studiesEl.addChildElement(s1);
            studiesEl.addChildElement(s2);
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }
    SOAPFactory soapFactory;
    public SOAPElement mockStudyEl(String id, String uid, String name) {
        try {
            SOAPElement studyEl = soapFactory.createElement("study");
            studyEl.addChildElement("identifier").setTextContent(id);
            studyEl.addChildElement("oid").setTextContent(uid);
            studyEl.addChildElement("name").setTextContent(name);
            return studyEl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Test
    public void contextLoads() {
    }


    @Test
    public void responseHandlerSimpleCase() {

        List<Study> studies = null;
        try {
            studies = ListStudiesResponseHandler.parseListStudiesResponse(mockedResponseListAllStudies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Number of studies found:" + studies.size());
        assert studies.size() == 3;
    }
    //TODO: We should put our Unit Tests here. Later if we have time we can also add Hamcrest Integration tests.

}
