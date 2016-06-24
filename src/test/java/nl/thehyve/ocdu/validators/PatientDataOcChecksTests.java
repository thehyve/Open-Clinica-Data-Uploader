package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import nl.thehyve.ocdu.validators.patientDataChecks.*;
import org.junit.Before;
import org.junit.Test;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


/**
 * Created by bo on 6/16/16.
 */
public class PatientDataOcChecksTests {

    MetaData metadata;

    @Before
    public void setup() {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            File testFile = new File("docs/responseExamples/Sjogren_STUDY1.xml");
            FileInputStream in = new FileInputStream(testFile);

            SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);
            this.metadata = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSuccess() {
        List<Subject> subjects = new ArrayList<>();
        PatientDataOcChecks ocChecks = new PatientDataOcChecks(metadata, subjects);
        List<ValidationErrorMessage> errors = ocChecks.getErrors();
        assertThat(errors, empty());
    }

    @Test
    public void testGenderFormat() {
        Subject subject = new Subject();
        subject.setSsid("1234");
        subject.setGender("wrongGenderFormat");

        GenderPatientDataCheck check = new GenderPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);

        assertThat(error.getMessage(), containsString("Gender"));
    }

    @Test
    public void testBirthdateFormatYearOnly() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //invalid format
        subject.setDateOfBirth("198x");
        DateOfBirthPatientDataCheck check = new DateOfBirthPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("invalid"));
    }

    @Test
    public void testBirthdateFormatFullDate() {
        Subject subject = new Subject();
        subject.setSsid("1234");
        metadata.setBirthdateRequired(1);
        //future birthday
        subject.setDateOfBirth("01-JUN-3012");
        DateOfBirthPatientDataCheck check = new DateOfBirthPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("past"));
    }

    @Test
    public void testDateOfEnrollmentEmpty() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //empty date of enrollment, today's date is used
        subject.setDateOfEnrollment("");
        DateOfEnrollmentPatientDataCheck check = new DateOfEnrollmentPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("Today"));
    }

    @Test
    public void testDateOfEnrollmentFuture() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //date of enrollment should be in the past
        subject.setDateOfEnrollment("01-JUN-3012");
        DateOfEnrollmentPatientDataCheck check = new DateOfEnrollmentPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("past"));
    }

    @Test
    public void testDateOfEnrollmentInvalidFormat() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //invalid date format
        subject.setDateOfEnrollment("01-JU");
        DateOfEnrollmentPatientDataCheck check = new DateOfEnrollmentPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("invalid"));
    }

    @Test
    public void testPersonIdProvided() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //person id is provided
        subject.setPersonId("1357");
        PersonIdPatientDataCheck check = new PersonIdPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("Person"));
    }


    @Test
    public void testSecondaryIdTooLong() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //secondary id is provided, but too long
        subject.setSecondaryId("1111112222222222333333333444444444445555555555666666666667777777777888888");
        SecondaryIdPatientDataCheck check = new SecondaryIdPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("length"));
    }

    @Test
    public void testStudyNotProvided() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //study is not provided
        subject.setStudy("");
        StudyPatientDataCheck check = new StudyPatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
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
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
        assertThat(error.getMessage(), containsString("exist"));
    }

    @Test
    public void testSiteExistSuccess() {
        Subject subject = new Subject();
        subject.setSsid("1234");

        //sites do not exist
        subject.setStudy("S_STUDY1");
        subject.setSite("Sjogren - Sjogren");
        SitePatientDataCheck check = new SitePatientDataCheck();
        ValidationErrorMessage error = check.getCorrespondingError(0, subject, metadata);
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
        ValidationErrorMessage error = check.getCorrespondingError(bogusLineNumber, subjectWithGender, metaData);
        assertThat(error, is(notNullValue()));
        assertThat(error.getMessage(), containsString("It is not allowed to upload gender by the study protocol"));
    }

    @Test
    public void bannedDobTest() throws Exception {
        MetaData metaData = new MetaData();
        int notRequired = 3;
        metaData.setBirthdateRequired(notRequired);
        Subject subjectWithDOB = new Subject();
        subjectWithDOB.setDateOfBirth("1997");
        DateOfBirthPatientDataCheck check = new DateOfBirthPatientDataCheck();
        int bogusLineNumber = 1;
        ValidationErrorMessage error = check.getCorrespondingError(bogusLineNumber, subjectWithDOB, metaData);
        assertThat(error, is(notNullValue()));
        assertThat(error.getMessage(), containsString("Date of birth submission is not allowed by the study protocol"));
    }

}
