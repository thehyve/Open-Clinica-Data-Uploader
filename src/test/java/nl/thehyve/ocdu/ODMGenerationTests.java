package nl.thehyve.ocdu;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.services.ODMService;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.validators.ClinicalDataOcChecks;
import org.junit.Before;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jacob on 6/9/16.
 */
public class ODMGenerationTests {

    MetaData metaData;
    List<StudySubjectWithEventsType> testSubjectWithEventsTypeList;
    OcUser testUser;
    UploadSession testSubmission;
    ClinicalDataFactory factory;

    ClinicalDataOcChecks clinicalDataOcChecks;

    Path testODMGenerationCorrect;

    @Before
    public void setUp() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        File testFile = new File("docs/responseExamples/getStudyMetadata3.xml");
        FileInputStream in = new FileInputStream(testFile);
        SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
        this.metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);

        this.testUser = new OcUser();
        this.testUser.setUsername("tester");
        this.testSubmission = new UploadSession("submission1", UploadSession.Step.MAPPING, new Date(), this.testUser);
        this.factory = new ClinicalDataFactory(testUser, testSubmission);

        this.testSubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();

        this.testODMGenerationCorrect = Paths.get("docs/exampleFiles/odmGeneration.txt");
    }

    @Test(expected = IllegalStateException.class)
    public void testSubjectOIDNotPresentInMap() throws Exception {
        List<ClinicalData> correctClinicalData = factory.createClinicalData(testODMGenerationCorrect);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, correctClinicalData, testSubjectWithEventsTypeList);
        ODMService odmService = new ODMService();
        Map<String, String> subjectLabelToOIDMap = new HashMap<>();
        subjectLabelToOIDMap.put("jan", null);
        odmService.generateODM(correctClinicalData, metaData, "Data Entry Complete", subjectLabelToOIDMap);
    }

    @Test
    public void testODMGeneration() throws Exception {
        List<ClinicalData> correctClinicalData = factory.createClinicalData(testODMGenerationCorrect);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, correctClinicalData, testSubjectWithEventsTypeList);
        ODMService odmService = new ODMService();
        Map<String, String> subjectLabelToOIDMap = new HashMap<>();
        subjectLabelToOIDMap.put("EV-00001", "SS_EV00001");
        subjectLabelToOIDMap.put("EV-00002", "SS_EV00002");
        String result = odmService.generateODM(correctClinicalData, metaData, "Data Entry Complete", subjectLabelToOIDMap);
        assertEquals(true, result.contains("c&quot;a&apos;r&lt;o&amp;t&gt;is"));
    }
}
