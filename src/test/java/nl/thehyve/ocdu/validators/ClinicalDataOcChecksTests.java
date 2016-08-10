package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.*;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.validators.clinicalDataChecks.ClinicalDataCrossCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.MultipleCrfCrossCheck;
import nl.thehyve.ocdu.validators.clinicalDataChecks.StudyStatusAvailable;
import nl.thehyve.ocdu.validators.fileValidators.DataPreMappingValidator;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
 * Units tests for the data validation
 * Created by piotrzakrzewski on 04/05/16.
 */

public class ClinicalDataOcChecksTests {

    private static ClinicalDataOcChecks clinicalDataOcChecks;
    private static MetaData metaData;
    private static List<StudySubjectWithEventsType> testSubjectWithEventsTypeList;
    private static OcUser testUser;
    private static UploadSession testSubmission;
    private static ClinicalDataFactory factory;
    private static Path testFileCorrect;
    private static Path testFileInCorrectSsidLength;
    private static Path testFileEventGapWithinData;
    private static Path testFileNonExistentEvent;
    private static Path testFileNonExistentCRF;
    private static Path testFileNonExistentStudy;
    private static Path testFileItemLengthExceeded;
    private static Path testFileNonExistentItem;
    private static Path testFileCorrectNoSite;
    private static Path testFileNonExistentVersion;
    private static Path testFileRangeCheckViolation;
    private static Path testFileTooManyValues;
    private static Path testFileTooManySignificantDigits;
    private static Path testFileDupSsid;
    private static Path testFileRepeatInNonrepeatingEvent;
    private static Path testFileMismatchingCRFVersion;
    private static Path testFileGroupRepeatError;
    private static Path emptyMandatory;
    private static Path missingToggle;
    private static Path hiddenVal;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            testUser = new OcUser();
            testUser.setUsername("tester");
            testSubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();
            testSubmission = new UploadSession("submission1", UploadSession.Step.MAPPING, new Date(), testUser);
            factory = new ClinicalDataFactory(testUser, testSubmission);

            testFileCorrect = Paths.get("docs/exampleFiles/data.txt");
            testFileInCorrectSsidLength = Paths.get("docs/exampleFiles/tooLongSSID.txt");
            testFileEventGapWithinData = Paths.get("docs/exampleFiles/eventGapInData.txt");
            testFileNonExistentEvent = Paths.get("docs/exampleFiles/nonExistentEvent.txt");
            testFileNonExistentCRF = Paths.get("docs/exampleFiles/nonExistentCrf.txt");
            testFileNonExistentStudy = Paths.get("docs/exampleFiles/nonexistentStudy.txt");
            testFileItemLengthExceeded = Paths.get("docs/exampleFiles/itemLengthExceeded.txt");
            testFileNonExistentItem = Paths.get("docs/exampleFiles/nonExistentItem.txt");
            testFileCorrectNoSite = Paths.get("docs/exampleFiles/data_no_site.txt");
            testFileNonExistentVersion = Paths.get("docs/exampleFiles/nonExistentVersion.txt");
            testFileRangeCheckViolation = Paths.get("docs/exampleFiles/rangeCheckViolation.txt");
            testFileTooManyValues = Paths.get("docs/exampleFiles/tooManyValues.txt");
            testFileTooManySignificantDigits = Paths.get("docs/exampleFiles/tooManySignificantDigits.txt");
            testFileDupSsid = Paths.get("docs/exampleFiles/dupSSID.txt");
            testFileRepeatInNonrepeatingEvent = Paths.get("docs/exampleFiles/event_repeat.txt");
            testFileMismatchingCRFVersion = Paths.get("docs/exampleFiles/mismatchingCrfVersionID.txt");
            testFileGroupRepeatError = Paths.get("docs/exampleFiles/group_repeat_error.txt");
            emptyMandatory = Paths.get("docs/exampleFiles/emptyMandatory.txt");
            missingToggle = Paths.get("docs/exampleFiles/missingToggle.txt");
            hiddenVal = Paths.get("docs/exampleFiles/hiddenVal.txt");

            MessageFactory messageFactory = MessageFactory.newInstance();
            File testFile = new File("docs/responseExamples/getStudyMetadata2.xml"); //TODO: Replace File with Path
            FileInputStream in = new FileInputStream(testFile);

            SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);//soapMessage;
            metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
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
    public void eventGapWithInData() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(testFileEventGapWithinData);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(EventGapError.class)));
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
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(isA(RangeCheckViolation.class)));
//        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
        ValidationErrorMessage errorMessage = errors.get(0);
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
        assertEquals(2, errors.size());
        assertThat(errors, hasItem(isA(TooManySignificantDigits.class)));
//        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
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
        assertThat(errors, hasSize(1));
        assertThat(errors, hasItem(isA(EventStatusNotAllowed.class)));
//        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
    }

    @Test
    public void partialDateValidation() throws Exception {
        String legit1 = "30-Oct-2000";
        assertThat(UtilChecks.isPDate(legit1), is(true));
        String legit2 = "Oct-2000";
        assertThat(UtilChecks.isPDate(legit2), is(true));
        String legit3 = "2000";
        assertThat(UtilChecks.isPDate(legit3), is(true));
        String invalid1 = "32-Dec-2000";
        assertThat(UtilChecks.isPDate(invalid1 ), is(false));
        String invalid2 = "10-XXX-2000";
        assertThat(UtilChecks.isPDate(invalid2 ), is(false));
        String invalid3 = "10-oct-2000";
        assertThat(UtilChecks.isPDate(invalid3 ), is(false));
        String invalid4 = "29-02-2001"; // not a leap year
        assertThat(UtilChecks.isPDate(invalid4 ), is(false));
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
        String invalid6 = "10-13-200Y";
        assertThat(UtilChecks.isDate(invalid6 ), is(false));
        String invalid7 = "10-10-200X";
        assertThat(UtilChecks.isDate(invalid7 ), is(false));
        String invalid8 = "29-02-2001";
        assertThat(UtilChecks.isDate(invalid8 ), is(false));
    }

    @Test
    @Ignore("Removed for acceptance testing")
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
        assertThat(errors, hasSize(1));
        assertThat(errors, hasItem(isA(ToggleVarForDisplayRuleAbsent.class)));
  //      assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
    }

    @Test
    public void hiddenValueTest() throws Exception {
        List<ClinicalData> incorrectClinicalData = factory.createClinicalData(hiddenVal);
        clinicalDataOcChecks = new ClinicalDataOcChecks(metaData, incorrectClinicalData, testSubjectWithEventsTypeList);
        List<ValidationErrorMessage> errors = clinicalDataOcChecks.getErrors();
        assertThat(errors, notNullValue());
        assertThat(errors, hasSize(1));
        assertThat(errors, hasItem(isA(HiddenValueError.class)));
//        assertThat(errors, hasItem(isA(MandatoryItemInCrfMissing.class)));
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
