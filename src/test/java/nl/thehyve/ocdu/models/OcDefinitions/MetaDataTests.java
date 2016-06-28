package nl.thehyve.ocdu.models.OcDefinitions;

import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link MetaData} class
 * Created by Jacob Rousseau on 28-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class MetaDataTests {

    private static MetaData metaData;

    @Test
    public void testFindCRFOID() {
        String formOID = metaData.findFormOID("", "0.10");
        assertEquals("", formOID);

        formOID = metaData.findFormOID("", "");
        assertEquals("", formOID);

        formOID = metaData.findFormOID("MUST-FOR_NON_TTP_STUDY", "");
        assertEquals("", formOID);

        formOID = metaData.findFormOID("MUST-FOR_NON_TTP_STUDY", "0.10");
        assertEquals("F_MUSTFOR_NON__010", formOID);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File testMetaDataFile = new File("docs/responseExamples/getStudyMetadata3.xml");
        FileInputStream in = new FileInputStream(testMetaDataFile);
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
        metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
    }
}
