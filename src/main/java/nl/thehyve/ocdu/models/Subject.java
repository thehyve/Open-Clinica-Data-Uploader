package nl.thehyve.ocdu.models;

import java.util.Date;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class Subject {

    private final String ssid;
    private final String gender;
    private final Date dateOfBirth;
    private final String personId;
    private final Date dateOfEnrollment;
    private final String secondaryId;
    private final Study study;

    public Subject(String ssid, String gender, Date dateOfBirth, String personId, Date dateOfEnrollment, String secondaryId, Study study) {
        this.ssid = ssid;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.personId = personId;
        this.dateOfEnrollment = dateOfEnrollment;
        this.secondaryId = secondaryId;
        this.study = study;
    }

    public String getSsid() {
        return ssid;
    }

    public String getGender() {
        return gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPersonId() {
        return personId;
    }

    public Date getDateOfEnrollment() {
        return dateOfEnrollment;
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    public Study getStudy() {
        return study;
    }


}
