package nl.thehyve.ocdu;

import nl.thehyve.ocdu.soap.ResponseHandlers.OCResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class AuthenticationTests {

    private Document testDocumentAuthSuccess;
    private Document testDocumentAuthFail;
    private Document authFailPureXML;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        this.authFailPureXML = docBuilder.parse(new File("docs/responseExamples/auth_error.xml"));
        MessageFactory messageFactory = MessageFactory.newInstance();
        File authSuccess = new File("docs/responseExamples/listStudiesResponse.xml"); //TODO: Replace File with Path
        File authFailure = new File("docs/responseExamples/auth_error.xml"); //TODO: Replace File with Path

        FileInputStream inSuccess = new FileInputStream(authSuccess);
        FileInputStream inFail = new FileInputStream(authFailure);

        this.testDocumentAuthSuccess = SoapUtils.toDocument(messageFactory.createMessage(null, inSuccess));
        this.testDocumentAuthFail = SoapUtils.toDocument(messageFactory.createMessage(null, inFail));
        inSuccess.close();
        inFail.close();
    }

    @Test
    public void isAuthenticatedTest1() throws Exception {
        boolean result = OCResponseHandler.isAuthFailure(testDocumentAuthFail);
        assertEquals(true, result);
    }

    @Test
    public void isAuthenticatedTest2() throws Exception {
        boolean result = OCResponseHandler.isAuthFailure(authFailPureXML);
        assertEquals(true, result);
    }

    @Test
    public void xpathExpressionTest1() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node faultNode = (Node) xpath.evaluate("//faultcode",
                authFailPureXML, XPathConstants.NODE);

        assertEquals(false, faultNode == null);
    }

    @Test
    public void xpathExpressionTest2() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node faultNode = (Node) xpath.evaluate("//incorrecttag",
                authFailPureXML, XPathConstants.NODE);

        assertEquals(true, faultNode == null);
    }
}
