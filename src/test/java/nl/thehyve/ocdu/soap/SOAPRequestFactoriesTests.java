package nl.thehyve.ocdu.soap;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import org.junit.Test;
import org.openclinica.ws.beans.GenderType;
import org.openclinica.ws.beans.SubjectType;

import javax.xml.datatype.XMLGregorianCalendar;

import static nl.thehyve.ocdu.soap.SOAPRequestFactories.StudySubjectFactory.createSubjectType;
import static nl.thehyve.ocdu.soap.SOAPRequestFactories.StudySubjectFactory.createXMLGregorianDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by piotrzakrzewski on 17/06/16.
 */

public class SOAPRequestFactoriesTests {
    @Test
    public void createXMLGregorianDateTest() throws Exception {
        Subject subject = new Subject();
        subject.setDateOfBirth("1996");
        XMLGregorianCalendar xmlGregorianDate = createXMLGregorianDate(subject.getDateOfBirth());
        int year = xmlGregorianDate.getYear();
        assertThat(year, equalTo(1996));
    }

    @Test
    public void createSubjectTypeTests_noGender_noDate() throws Exception {
        Subject subject = new Subject();
        SubjectType subjectType = createSubjectType(subject);
        assertThat(subjectType , notNullValue());
        assertThat(subjectType, hasProperty("gender", nullValue()));
        assertThat(subjectType, hasProperty("dateOfBirth", nullValue()));
    }

    @Test
    public void createSubjectTypeTests_noDate() throws Exception {
        Subject subject = new Subject();
        subject.setGender("f");
        SubjectType subjectType = createSubjectType(subject);
        assertThat(subjectType , notNullValue());
        assertThat(subjectType, hasProperty("gender", is(GenderType.F)));
        assertThat(subjectType, hasProperty("dateOfBirth", nullValue()));
    }

    @Test
    public void createSubjectTypeTests_noGender() throws Exception {
        Subject subject = new Subject();
        subject.setDateOfBirth("2000");
        SubjectType subjectType = createSubjectType(subject);
        assertThat(subjectType , notNullValue());
        assertThat(subjectType, hasProperty("gender", nullValue()));
        assertThat(subjectType, hasProperty("dateOfBirth", notNullValue(XMLGregorianCalendar.class)));
    }

    @Test
    public void createSubjectTypeTests_allSet() throws Exception {
        Subject subject = new Subject();
        subject.setGender("f");
        subject.setDateOfBirth("2000");
        SubjectType subjectType = createSubjectType(subject);
        assertThat(subjectType , notNullValue());
        assertThat(subjectType, hasProperty("gender", is(GenderType.F)));
        assertThat(subjectType, hasProperty("dateOfBirth", notNullValue(XMLGregorianCalendar.class)));
    }



}
