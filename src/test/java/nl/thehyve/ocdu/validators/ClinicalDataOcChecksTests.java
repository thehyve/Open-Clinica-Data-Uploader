package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.*;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.validators.clinicalDataChecks.*;
import nl.thehyve.ocdu.validators.fileValidators.DataPreMappingValidator;
import org.junit.Before;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static nl.thehyve.ocdu.TestUtils.incorrectEventStatusExample;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */

public class ClinicalDataOcChecksTests {

    ClinicalDataOcChecks clinicalDataOcChecks;
    MetaData metaData;
    List<StudySubjectWithEventsType> testSubjectWithEventsTypeList;
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
    Path testFileTooManyValues;
    Path testFileTooManySignificantDigits;
    Path testFileDupSsid;
    Path testFileRepeatInNonrepeatingEvent;
    Path testFileMismatchingCRFVersion;
    Path testFileGroupRepeatError;
    Path emptyMandatory;
    Path missingToggle;
    Path hiddenVal;

    @Before
    public void setUp() throws Exception {
        try {
            this.testUser = new OcUser();
            this.testUser.setUsername("tester");
            this.testSubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();
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
            this.testFileTooManyValues = Paths.get("docs/exampleFiles/tooManyValues.txt");
            this.testFileTooManySignificantDigits = Paths.get("docs/exampleFiles/tooManySignificantDigits.txt");
            this.testFileDupSsid = Paths.get("docs/exampleFiles/dupSSID.txt");
            this.testFileRepeatInNonrepeatingEvent = Paths.get("docs/exampleFiles/event_repeat.txt");
            this.testFileMismatchingCRFVersion = Paths.get("docs/exampleFiles/mismatchingCrfVersionID.txt");
            this.testFileGroupRepeatError = Paths.get("docs/exampleFiles/group_repeat_error.txt");
            this.emptyMandatory = Paths.get("docs/exampleFiles/emptyMandatory.txt");
            this.missingToggle = Paths.get("docs/exampleFiles/missingToggle.txt");
            this.hiddenVal = Paths.get("docs/exampleFiles/hiddenVal.txt");

            MessageFactory messageFactory = MessageFactory.newInstance();
            File testFile = new File("docs/responseExamples/getStudyMetadata2.xml"); //TODO: Replace File with Path
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
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, correctClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void tooLongSSID() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileInCorrectSsidLength);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(SSIDTooLong.class)));
    }

    @Test
    public void nonExistentEvent() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentEvent);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(2, errors.size());
        assertThat(errors, hasItem(isA(CrfCouldNotBeVerified.class)));
        assertThat(errors, hasItem(isA(EventDoesNotExist.class)));
        // We expect one error because of Event which does
        // not exist and one error for CRF
    }

    @Test
    public void nonExistentCRF() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentCRF);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(CRFDoesNotExist.class)));
    }

    @Test
    public void itemLengthExceeded() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileItemLengthExceeded);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(2, errors.size());
        assertThat(errors, hasItem(isA(FieldLengthExceeded.class)));
        assertThat(errors, hasItem(isA(EnumerationError.class)));
    }

    @Test
    public void nonExistentItem() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentItem);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(ItemDoesNotExist.class)));
    }

    @Test
    public void correctFileWithoutSIte() throws Exception {
        List<ClinicalData> correctClinicalData = factory.createClinicalData(testFileCorrectNoSite);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, correctClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(0, errors.size());
    }


    @Test
    public void nonExistentVersion() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentVersion);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(CRFDoesNotExist.class)));
    }

    @Test
    public void rangeCheckViolation() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileRangeCheckViolation);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(2, errors.size());
        assertThat(errors, hasItem(isA(RangeCheckViolation.class)));
        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
        ValidationErrorMessage errorMessage = errors.get(1);
        assertThat(errorMessage.getOffendingValues(), hasSize(3));
    }

    @Test
    public void tooManyValues() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileTooManyValues);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(2, errors.size());
        assertThat(errors, hasItem(isA(TooManyValues.class)));
        assertThat(errors, hasItem(isA(FieldLengthExceeded.class)));
    }

    @Test
    public void tooManySignificantDigits() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileTooManySignificantDigits);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(3, errors.size());
        assertThat(errors, hasItem(isA(TooManySignificantDigits.class)));
        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
        assertThat(errors, hasItem(isA(FieldLengthExceeded.class)));
    }

    @Test
    public void duplicatedSsid() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileDupSsid);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(SSIDDuplicated.class)));
    }

    @Test
    public void repeatInNonrepeatingEvent() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileRepeatInNonrepeatingEvent);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(RepeatInNonrepeatingEvent.class)));
    }

    @Test
    public void versionMismatchCRF() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileMismatchingCRFVersion);
        File testFile = new File("docs/responseExamples/getStudyMetadata3.xml");
        FileInputStream in = new FileInputStream(testFile);
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
        MetaData crfVersionMetaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
        clinicalDataOcChecks = new ClinicalDataOcChecks(crfVersionMetaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        in.close();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(CRFVersionMismatchError.class)));
    }

    @Test
    public void repeatInNonrepeatingGroup() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileGroupRepeatError);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(RepeatInNonrepeatingItem.class)));
    }

    @Test
    public void studyStatusTest() throws Exception {
        ClinicalDataCrossCheck statusCheck = new StudyStatusAvailable();
        MetaData metaData = new MetaData();
        metaData.setStatus("whatever");
        ValidationErrorMessage correspondingError = statusCheck.getCorrespondingError(null, metaData, null, null, null, null);
        assertThat(correspondingError, is(notNullValue()));
        assertThat(correspondingError, is(instanceOf(StudyStatusError.class)));
    }

    @Test
    public void dataPreMappingValidatorTest() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileNonExistentCRF);
        DataPreMappingValidator validator = new DataPreMappingValidator(metaData, incorrectClinicalData, Collections.emptyList());
        List<ValidationErrorMessage> errors = validator.getErrors();
        assertEquals(2, errors.size());
        assertThat(errors, hasItem(isA(CRFDoesNotExist.class)));
        assertThat(errors, hasItem(isA(SiteDoesNotExist.class)));
    }

    @Test
    public void eventStatusCheck() throws Exception {
        File testFile = new File("docs/responseExamples/getStudyMetadata3.xml");
        FileInputStream in = new FileInputStream(testFile);
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
        MetaData crfVersionMetaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
        List<StudySubjectWithEventsType> incorrectEventStatus = incorrectEventStatusExample();
        List<ClinicalData> incorrectData = new ArrayList<>();
        ClinicalData dPoint = new ClinicalData("Eventful", "age", "ssid1",
                "RepeatingEvent", 1, "MUST-FOR_NON_TTP_STUDY", null, "0.08", null, null, "12");
        incorrectData.add(dPoint);
        clinicalDataOcChecks = new ClinicalDataOcChecks(crfVersionMetaData, incorrectData, incorrectEventStatus);
        in.close();
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertThat(errors, hasSize(2));
        assertThat(errors, hasItem(isA(EventStatusNotAllowed.class)));
        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
    }

    @Test
    public void partialDateValidation() throws Exception {
        String bogusPdate = "Oct-200G";
        boolean pDate = UtilChecks.isPDate(bogusPdate);
        assertThat(pDate, is(false));
        String legitmatedate = "Oct-2000";
        assertThat(UtilChecks.isPDate(legitmatedate), is(true));
        String legitmatedate2 = "29-Oct-2000";
        assertThat(UtilChecks.isPDate(legitmatedate2), is(true));
        String invalidPdate2 = "32-Dec-2000";
        assertThat(UtilChecks.isPDate(invalidPdate2), is(false));
        String invalidPdate3 = "10-XXX-2000";
        assertThat(UtilChecks.isPDate(invalidPdate3 ), is(false));
    }

    @Test
    public void dateValidation() throws Exception {
        String legitmatedate = "22-06-2000";
        assertThat(UtilChecks.isDate(legitmatedate), is(true));
        String invalid = "32-12-2000";
        assertThat(UtilChecks.isDate(invalid), is(false));
        String invalid2 = "20-Oct-2000";
        assertThat(UtilChecks.isDate(invalid2 ), is(false));
        String invalid3 = "10-2000";
        assertThat(UtilChecks.isDate(invalid3 ), is(false));
        String invalid4 = "2000";
        assertThat(UtilChecks.isDate(invalid4 ), is(false));
        String invalid5 = "10-13-2000";
        assertThat(UtilChecks.isDate(invalid5 ), is(false));
    }

    @Test
    public void ignoredMandatoryItem() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(emptyMandatory);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(1));
        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
    }

    @Test
    public void missingToggleTest() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(missingToggle);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(2));
        assertThat(errors, hasItem(isA(ToggleVarForDisplayRuleAbsent.class)));
        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
    }

    @Test
    public void hiddenValueTest() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(hiddenVal);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(2));
        assertThat(errors, hasItem(isA(HiddenValueError.class)));
        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
    }

    @Test
    public void moreThanOneCrf() throws Exception {
        List<ClinicalData> incorrectData = new ArrayList<>();
        ClinicalData clinicalData1 = new ClinicalData();
        ClinicalData clinicalData2 = new ClinicalData();
        incorrectData.add(clinicalData1);
        incorrectData.add(clinicalData2);
        clinicalData1.setCrfName("crf1");
        clinicalData1.setCrfVersion("v1");
        clinicalData2.setCrfName("crf2");
        clinicalData2.setCrfVersion("v1");
        MultipleCrfCrossCheck check = new MultipleCrfCrossCheck();
        ValidationErrorMessage correspondingError = check.getCorrespondingError(incorrectData, null, null, null, null, null);
        assertThat(correspondingError, notNullValue());
    }
}
