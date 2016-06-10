package nl.thehyve.ocdu;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.services.ODMService;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.soap.ResponseHandlers.ListAllByStudyResponseHandler;
import nl.thehyve.ocdu.validators.ClinicalDataOcChecks;
import org.junit.Before;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

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

        this.testSubjectWithEventsTypeList = createStudySubjectWithEventList();

        this.testODMGenerationCorrect = Paths.get("docs/exampleFiles/odmGeneration.txt");
    }

    private List<StudySubjectWithEventsType> createStudySubjectWithEventList() throws Exception {
        File mockResponseListAllByStudyFile = new File("docs/responseExamples/listAllByStudyResponse.xml");
        InputStream mockResponseListAllByStudyFileInputStream = new FileInputStream(mockResponseListAllByStudyFile);

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage(new MimeHeaders(), mockResponseListAllByStudyFileInputStream);
        return ListAllByStudyResponseHandler.retrieveStudySubjectsType(soapMessage);
    }


    @Test
    public void testODMGeneration() throws Exception {
        List<ClinicalData> correctClinicalData = factory.createClinicalData(testODMGenerationCorrect);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, correctClinicalData, testSubjectWithEventsTypeList);
        ODMService odmService = new ODMService();

        String result = odmService.generateODM(correctClinicalData, metaData);

        System.out.println(result);

    }
}
