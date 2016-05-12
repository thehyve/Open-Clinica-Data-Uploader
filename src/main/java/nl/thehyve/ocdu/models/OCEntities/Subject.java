package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */

@Entity
public class Subject implements OcEntity, UserSubmitted {

    private String ssid;
    private String gender;
    private Date dateOfBirth;
    @Column(columnDefinition = "TEXT")
    private String personId;
    private Date dateOfEnrollment;
    @Column(columnDefinition = "TEXT")
    private String secondaryId;
    @Column(columnDefinition = "TEXT")
    private String study;

    @ManyToOne()
    private OcUser owner;
    @ManyToOne()
    private UploadSession submission;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public Subject() {
    }


    public OcUser getOwner() {
        return owner;
    }

    public UploadSession getSubmission() {
        return submission;
    }

    @Override
    public void setOwner(OcUser owner) {
        this.owner = owner;
    }

    @Override
    public void setSubmission(UploadSession submission) {
        this.submission = submission;
    }

    public long getId() {
        return id;
    }

    @Override
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

    @Override
    public String getStudy() {
        return study;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "study='" + study + '\'' +
                ", ssid='" + ssid + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", personId='" + personId + '\'' +
                ", dateOfEnrollment=" + dateOfEnrollment +
                ", secondaryId='" + secondaryId + '\'' +
                '}';
    }
}
