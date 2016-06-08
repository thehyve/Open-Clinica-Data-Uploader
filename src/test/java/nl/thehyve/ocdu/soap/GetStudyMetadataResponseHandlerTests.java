package nl.thehyve.ocdu.soap;

import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

public class GetStudyMetadataResponseHandlerTests {

    static SOAPMessage getStudyMetadata3;

    @Test
    public void testMeteDataStudyInformationRetrieval() throws Exception {
        MetaData metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(getStudyMetadata3);

        assertThat(metaData, allOf(
                hasProperty("studyOID", is("S_EVENTFUL")),
                hasProperty("studyName", is("Eventful"))
        ));
    }

    @Test
    public void testMeteDataEventDefinitionsRetrieval() throws Exception {
        MetaData metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(getStudyMetadata3);

        assertThat(metaData, allOf(
                hasProperty("eventDefinitions", containsInAnyOrder(
                        allOf(
                                hasProperty("name", is("Non-repeating Event")),
                                hasProperty("studyEventOID", is("SE_EVENTFUL")),
                                hasProperty("repeating", is(false)),
                                hasProperty("type", is("Scheduled"))
                        ),
                        allOf(
                                hasProperty("name", is("RepeatingEvent")),
                                hasProperty("studyEventOID", is("SE_REPEATINGEVENT")),
                                hasProperty("repeating", is(true)),
                                hasProperty("type", is("Scheduled"))
                        )
                ))
        ));
    }

    @Test
    public void testMeteDataSiteDefinitionsRetrieval() throws Exception {
        MetaData metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(getStudyMetadata3);

        assertThat(metaData, allOf(
                hasProperty("siteDefinitions", contains(
                        allOf(
                                hasProperty("siteOID", is("S_EVENTFUL_4878")),
                                hasProperty("name", is("Eventful - Eventful-Site"))
                        )
                ))
        ));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        Path getStudyMetadata3FilePath = Paths.get("docs/responseExamples/getStudyMetadata3.xml");
        GetStudyMetadataResponseHandlerTests.getStudyMetadata3 = messageFactory.createMessage(null,
                new FileInputStream(getStudyMetadata3FilePath.toFile()));
    }
}
