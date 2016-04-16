package nl.thehyve.ocdu.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */

@Entity
public class Subject {

    private String ssid;
    private String gender;
    private Date dateOfBirth;
    private String personId;
    private Date dateOfEnrollment;
    private String secondaryId;
    private Study study;
    private String owner;
    private String submission;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    protected Subject() {
    }

    public Subject(String ssid, String gender, Date dateOfBirth, String personId, Date dateOfEnrollment, String secondaryId, Study study, String owner, String submission) {
        this.ssid = ssid;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.personId = personId;
        this.dateOfEnrollment = dateOfEnrollment;
        this.secondaryId = secondaryId;
        this.study = study;
        this.owner = owner;
        this.submission = submission;
    }

    public String getOwner() {
        return owner;
    }

    public String getSubmission() {
        return submission;
    }

    public long getId() {
        return id;
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
