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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

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
            FileInputStream in = new FileInputStream(new File("docs/responseExamples/listStudiesResponse.xml"));

            mockedResponseListAllStudies = messageFactory.createMessage(null, in);;//soapMessage;
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
        assertEquals(2, studies.size());
    }

    @Test
    public void xpathTest() {
        try {
            Document document = toDocument(mockedResponseListAllStudies);
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList studyNodes = (NodeList) xpath.evaluate("//listAllResponse/studies/study", document, XPathConstants.NODESET);
            assertEquals(2, studyNodes.getLength());
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }


}
