package nl.thehyve.ocdu;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.validators.ClinicalDataOcChecks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class ValidationTests {

    ClinicalDataOcChecks clinicalDataOcChecks;
    MetaData metaData;
    OcUser testUser;
    UploadSession testSubmission;
    ClinicalDataFactory factory;
    Path testFileCorrect;
    Path testFileInCorrectSsidLength;
    Path testFileNonExistentEvent;
    Path testFileNonExistentCRF;
    Path testFileNonExistentStudy;
    Path testFileItemLengthExceeded;
    Path testFileNonExistentItem;
    Path testFileCorrectNoSite;
    Path testFileNonExistentVersion;
    Path testFileRangeCheckViolation;

    @Before
    public void setUp() throws Exception {
        try {
            this.testUser = new OcUser();
            this.testUser.setUsername("tester");
            this.testSubmission = new UploadSession("submission1", UploadSession.Step.MAPPING, new Date(), this.testUser);
            this.factory = new ClinicalDataFactory(testUser, testSubmission);

            this.testFileCorrect = Paths.get("docs/exampleFiles/data.txt");
            this.testFileInCorrectSsidLength = Paths.get("docs/exampleFiles/tooLongSSID.txt");
            this.testFileNonExistentEvent = Paths.get("docs/exampleFiles/nonExistentEvent.txt");
            this.testFileNonExistentCRF = Paths.get("docs/exampleFiles/nonExistentCrf.txt");
            this.testFileNonExistentStudy = Paths.get("docs/exampleFiles/nonexistentStudy.txt");
            this.testFileItemLengthExceeded = Paths.get("docs/exampleFiles/itemLengthExceeded.txt");
            this.testFileNonExistentItem = Paths.get("docs/exampleFiles/nonExistentItem.txt");
            this.testFileCorrectNoSite = Paths.get("docs/exampleFiles/data_no_site.txt");
            this.testFileNonExistentVersion = Paths.get("docs/exampleFiles/nonExistentVersion.txt");
            this.testFileRangeCheckViolation = Paths.get("docs/exampleFiles/rangeCheckViolation.txt");

            MessageFactory messageFactory = MessageFactory.newInstance();
            File testFile = new File("docs/responseExamples/getStudyMetadata.xml"); //TODO: Replace File with Path
            FileInputStream in = new FileInputStream(testFile);

            SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
            this.metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void validateCorrectClinicalDataTest() throws Exception {
        List<ClinicalData> correctClinicalData = factory.createClinicalData(testFileCorrect);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, correctClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void tooLongSSID() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileInCorrectSsidLength);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
    }

    @Test
    public void nonExistentEvent() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentEvent);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(2, errors.size()); // We expect one error because of Event which does
        // not exist and one error for CRF
    }

    @Test
    public void nonExistentCRF() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentCRF);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
    }

    @Test
    public void itemLengthExceeded() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileItemLengthExceeded);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
    }

    @Test
    public void nonExistentItem() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentItem);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
    }

    @Test
    public void correctFileWithoutSIte() throws Exception {
        List<ClinicalData> correctClinicalData = factory.createClinicalData(testFileCorrectNoSite);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, correctClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(0, errors.size());
    }


    @Test
    public void nonExistentVersion() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentVersion);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
    }

    @Test
    public void rangeCheckViolation() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileRangeCheckViolation);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
    }

    @Test
    public void tooManyValues() throws Exception {


    }
}
