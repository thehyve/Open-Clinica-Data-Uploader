package nl.thehyve.ocdu;

import nl.thehyve.ocdu.models.MetaData;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by piotrzakrzewski on 02/05/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class GetMetadataTests {

    private static final Logger log = LoggerFactory.getLogger(GetMetadataTests.class);

    SOAPMessage mockedResponseGetMetadata;
    private File testFile;

    @Before
    public void setUp() {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            this.testFile = new File("docs/responseExamples/getStudyMetadata.xml"); //TODO: Replace File with Path
            FileInputStream in = new FileInputStream(testFile);

            mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testFileExists() throws Exception {
        assertEquals(true, testFile.exists());
    }

    @Test
    public void responseHandlerSimpleCase() throws Exception {
        MetaData metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
        assertThat(metaData, is(notNullValue()));
    }

}
