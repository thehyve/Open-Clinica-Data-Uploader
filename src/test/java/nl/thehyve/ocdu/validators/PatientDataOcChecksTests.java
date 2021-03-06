package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.TestUtils;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.ProtocolFieldRequirementSetting;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.validators.patientDataChecks.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


/**
 * Created by bo on 6/16/16.
 */
public class PatientDataOcChecksTests {

    private static MetaData metadata;
    private static List<StudySubjectWithEventsType> testSubjectWithEventsTypeList;
    private static Set<String> presentInData;

    @BeforeClass
    public static void setup() {
        try {
            testSubjectWithEventsTypeList = TestUtils.createStudySubjectWithEventList();
            MessageFactory messageFactory = MessageFactory.newInstance();
            File testFile = new File("docs/responseExamples/Sjogren_STUDY1.xml");
            FileInputStream in = new FileInputStream(testFile);

            SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);
            metadata = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
            presentInData = new HashSet<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSuccess() {
        List<Subject> subjects = new ArrayList<>();
        PatientDataOcChecks ocChecks = new PatientDataOcChecks(metadata, subjects, testSubjectWithEventsTypeList, presentInData);
        List<ValidationErrorMessage> errors = ocChecks.getErrors();
        assertThat(errors, empty());
    }

    @Test
    public void testGenderFormat() {
        Subject subject = new Subject();
        subject.setSsid("1234");
        subject.setGender("wrongGenderFormat");

        GenderPatientDataCheck check = new GenderPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);

        assertThat(error.getMessage(), containsString("Gender"));
    }

    @Test
    public void testBirthdateFormatYearOnly() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //invalid format
        subject.setDateOfBirth("198x");
        DateOfBirthPatientDataCheck check = new DateOfBirthPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("invalid"));
    }

    @Test
    public void testBirthdateFormatFullDate() {
        Subject subject = new Subject();
        subject.setSsid("1234");
        metadata.setBirthdateRequired(1);
        //future birthday
        subject.setDateOfBirth("01-06-3012");
        DateOfBirthPatientDataCheck check = new DateOfBirthPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("past"));

        subject.setDateOfBirth(null);
        error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("Date of birth is missing"));
    }

    @Test
    public void testDateOfEnrollmentEmpty() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //empty date of enrollment, today's date is used
        subject.setDateOfEnrollment("");
        DateOfEnrollmentPatientDataCheck check = new DateOfEnrollmentPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("Today"));
    }

    @Test
    public void testDateOfEnrollmentFuture() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //date of enrollment should be in the past
        subject.setDateOfEnrollment("01-06-3012");
        DateOfEnrollmentPatientDataCheck check = new DateOfEnrollmentPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("past"));
    }

    @Test
    public void testDateOfEnrollmentInvalidFormat() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //invalid date format
        subject.setDateOfEnrollment("01-JU");
        DateOfEnrollmentPatientDataCheck check = new DateOfEnrollmentPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("invalid"));
    }

    @Test
    public void testPersonIdProvided() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //person id is provided
        metadata.setPersonIDUsage(ProtocolFieldRequirementSetting.MANDATORY);
        subject.setPersonId("");
        PersonIdPatientDataCheck check = new PersonIdPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("Person"));

        metadata.setPersonIDUsage(ProtocolFieldRequirementSetting.OPTIONAL);
        error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertEquals(error, null);

        metadata.setPersonIDUsage(ProtocolFieldRequirementSetting.BANNED);
        error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertEquals(error, null);

        metadata.setPersonIDUsage(ProtocolFieldRequirementSetting.MANDATORY);
        subject.setPersonId("1345");
        error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertEquals(error, null);

        metadata.setPersonIDUsage(ProtocolFieldRequirementSetting.OPTIONAL);
        error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertEquals(error, null);

        metadata.setPersonIDUsage(ProtocolFieldRequirementSetting.BANNED);
        error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertEquals(error, null);

    }


    @Test
    public void testSecondaryIdTooLong() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //secondary id is provided, but too long
        subject.setSecondaryId("1111112222222222333333333444444444445555555555666666666667777777777888888");
        SecondaryIdPatientDataCheck check = new SecondaryIdPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("length"));
    }

    @Test
    public void testStudyNotProvided() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //study is not provided
        subject.setStudy("");
        StudyPatientDataCheck check = new StudyPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("Study should"));
    }

    @Test
    public void testSitesNotExist() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //sites do not exist
        subject.setStudy("S_STUDY1");
        subject.setSite("myownsitethatdoesnotexist");
        SitePatientDataCheck check = new SitePatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat(error.getMessage(), containsString("exist"));
    }

    @Test
    public void testSiteExistSuccess() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //sites do not exist
        subject.setStudy("S_STUDY1");
        subject.setSite("SjogrenSjogren");
        SitePatientDataCheck check = new SitePatientDataCheck();
        List<SiteDefinition> siteDefs = new ArrayList<>();
        SiteDefinition sjogrenSite = new SiteDefinition();
        sjogrenSite.setSiteOID("SjogrenSjogren");
        siteDefs.add(sjogrenSite);
        metadata.setSiteDefinitions(siteDefs);
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertNull(error);

    }

    @Test
    public void bannedGenderTest() throws Exception {
        MetaData metaData = new MetaData();

        metaData.setGenderRequired(false);
        Subject subjectWithGender = new Subject();
        subjectWithGender.setGender("m");
        GenderPatientDataCheck check = new GenderPatientDataCheck();
        int bogusLineNumber = 1;
        ValidationErrorMessage error = check.getCorrespondingError(bogusLineNumber, subjectWithGender, metaData,
                testSubjectWithEventsTypeList, presentInData);
        assertThat(error, is(notNullValue()));
        assertThat(error.getMessage(), containsString("It is not allowed to upload gender by the study protocol"));
    }

    @Test
    public void bannedDobTest() throws Exception {
        MetaData metaData = new MetaData();
        int notRequired = 3;
        metaData.setBirthdateRequired(notRequired);
        Subject subjectWithDOB = new Subject();
        Subject subjectWithDOBFull = new Subject();
        subjectWithDOBFull.setDateOfBirth("01-JUN-2000");
        subjectWithDOB.setDateOfBirth("1997");
        DateOfBirthPatientDataCheck check = new DateOfBirthPatientDataCheck();
        int bogusLineNumber = 1;
        ValidationErrorMessage error = check.getCorrespondingError(bogusLineNumber, subjectWithDOB, metaData, testSubjectWithEventsTypeList, presentInData);
        ValidationErrorMessage errorFullYear = check.getCorrespondingError(bogusLineNumber, subjectWithDOBFull, metaData, testSubjectWithEventsTypeList, presentInData);
        assertThat(error, is(notNullValue()));
        assertThat(error.getMessage(), containsString("Date of birth submission is not allowed by the study protocol"));
        assertThat(errorFullYear.getMessage(), is(notNullValue()));
        assertThat(errorFullYear.getMessage(), containsString("Date of birth submission is not allowed by the study protocol"));
    }

    @Test
    public void subjectAlreadyRegisteredTest() throws Exception {
        Subject subject = new Subject();
        subject.setSsid("EV-00002");

        SubjectNotRegistered check = new SubjectNotRegistered();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);

        assertThat(error.getMessage(), containsString("already registered"));
    }

    @Test
    public void subjectPresentInTheData() throws Exception {
        Subject subject = new Subject();
        String s1 = "EV-00002";
        subject.setSsid(s1);

        PresentInData check = new PresentInData();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);

        assertThat(error.getMessage(), containsString("Absent in the data file"));
        presentInData.add(s1);
        error = check.getCorrespondingError(0, subject, metadata, testSubjectWithEventsTypeList, presentInData);
        assertThat("Returns null for subject present in the data-file", error, nullValue());
    }
}
