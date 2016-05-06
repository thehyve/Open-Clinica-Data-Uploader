package nl.thehyve.ocdu;

import com.sun.org.apache.xpath.internal.NodeSet;
import nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;

/**
 * Created by piotrzakrzewski on 03/05/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class SoapUtilsTests {

    private SOAPMessage mockedResponseGetMetadata;
    private File testFile;

    @Before
    public void setUp() {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            this.testFile = new File("docs/responseExamples/getStudyMetadata.xml"); //TODO: Replace File with Path
            FileInputStream in = new FileInputStream(testFile);

            this.mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void unescapeCdataXMLTest() throws Exception {
        Document document = SoapUtils.toDocument(mockedResponseGetMetadata);
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node odmNode = (Node) xpath.evaluate("//createResponse/odm", document, XPathConstants.NODE);
        String textContent = odmNode.getTextContent();
        Document unescapedDocument = SoapUtils.unEscapeCDATAXML(textContent);
        NodeList crfNodes = (NodeList) xpath.evaluate("//MetaDataVersion/FormDef", unescapedDocument,
                XPathConstants.NODESET);
        assertThat(unescapedDocument, is(notNullValue()));
        assertEquals(true, crfNodes.getLength() > 0);
    }
}
