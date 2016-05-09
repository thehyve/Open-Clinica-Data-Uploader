package nl.thehyve.ocdu;

import nl.thehyve.ocdu.models.OcDefinitions.*;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
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

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by piotrzakrzewski on 02/05/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class GetMetadataTests {

    private static final Logger log = LoggerFactory.getLogger(GetMetadataTests.class);

    private SOAPMessage mockedResponseGetMetadata;
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
    public void xpathSelectorTest() throws Exception {
        String selector = GetStudyMetadataResponseHandler.presentInEventSelector;
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document odm = GetStudyMetadataResponseHandler.getOdm(mockedResponseGetMetadata);
        NodeList crfNodes = (NodeList) xpath.evaluate(selector,
                odm, XPathConstants.NODESET);
        assertEquals(true, crfNodes.getLength() > 0);

    }

    @Test
    public void xpathCRfVersion() throws Exception {
        String selector = GetStudyMetadataResponseHandler.CRF_VERSION_SELECTOR;
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document odm = GetStudyMetadataResponseHandler.getOdm(mockedResponseGetMetadata);
        String  versions= (String) xpath.evaluate(selector,
                odm, XPathConstants.STRING);
        assertThat(versions, is(notNullValue()));

    }

    @Test
    public void responseHandlerSimpleCase() throws Exception {
        MetaData metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
        assertThat(metaData, is(notNullValue()));

        List<EventDefinition> eventDefinitions = metaData.getEventDefinitions();
        assertThat(eventDefinitions
                ,
                everyItem(is(allOf(notNullValue(), instanceOf(EventDefinition.class)))));
        assertEquals(2, eventDefinitions.size());

        EventDefinition eventDefinition = eventDefinitions.get(1);
        assertEquals("SE_NONREPEATINGEVENT", eventDefinition.getStudyEventOID());
        assertEquals("Non-repeating event", eventDefinition.getName());

        List<CRFDefinition> crfDefinitions = eventDefinition.getCrfDefinitions();

        assertEquals(7, crfDefinitions.size());
        crfDefinitions.forEach(crDef -> {
            assertThat(crDef.getVersion(), is(notNullValue()));
                }
        );
        List<ItemGroupDefinition> itemGroupDefinitions = metaData.getItemGroupDefinitions();
        assertEquals(24, itemGroupDefinitions.size());

        int totalExpectedItemDefs = 166;
        List<ItemDefinition> allItemdefs = new ArrayList<>();
        itemGroupDefinitions.forEach(itemGroupDefinition -> {
            assertThat(itemGroupDefinition.getOid(), is(notNullValue()) );
            assertThat(itemGroupDefinition.getName(), is(notNullValue()) );
            assertThat(itemGroupDefinition.getItems(), is(notNullValue()) );
            List<ItemDefinition> items = itemGroupDefinition.getItems();
            assertTrue(items.size() > 0);
            allItemdefs.addAll(items);
            items.stream().forEach(item -> assertTrue(isUnique(item, items)));
        });
        assertEquals(totalExpectedItemDefs, allItemdefs.size());
    }


    private boolean isUnique(ItemDefinition item, List<ItemDefinition> collection ) {
        long count = collection.stream().filter(itemDefinition -> itemDefinition.getOid().equals(item.getOid())).count();
        return count == 1;
    }

}
