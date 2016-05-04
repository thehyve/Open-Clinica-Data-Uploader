package nl.thehyve.ocdu;

import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListStudiesResponseHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class ListStudiesResponseTests {

    private static final Logger log = LoggerFactory.getLogger(ListStudiesResponseTests.class);

    SOAPMessage mockedResponseListAllStudies;
    private File testFile;

    @Before
    public void setUp() {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            this.testFile = new File("docs/responseExamples/listStudiesResponse.xml"); //TODO: Replace File with Path
            FileInputStream in = new FileInputStream(testFile);

            mockedResponseListAllStudies = messageFactory.createMessage(null, in);//soapMessage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testFileExists() throws Exception {
        assertEquals(true, testFile.exists());
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
        assertThat(
                studies,
                everyItem(is(allOf(notNullValue(), instanceOf(Study.class)))));
        assertThat(studies.get(0), hasProperty("identifier", equalTo("Study 1")));
        assertThat(studies.get(0), hasProperty("oid", equalTo("S_STUDY1")));
        assertThat(studies.get(0), hasProperty("name", equalTo("Test Study 1")));
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
