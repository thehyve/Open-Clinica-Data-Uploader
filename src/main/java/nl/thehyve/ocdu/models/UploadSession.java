package nl.thehyve.ocdu.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by piotrzakrzewski on 28/04/16.
 */
@Entity
public class UploadSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @ManyToOne()
    private OcUser owner;

    public enum Step {
        MAPPING, FEEDBACK_DATA, SUBJECTS, FEEDBACK_SUBJECTS, EVENTS, FEEDBACK_EVENTS, OVERVIEW
    }

    private Step step;
    private Date savedDate;
    private String study;

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public UploadSession() {
        // needed by hibernate
    }

    public UploadSession(String name, Step step, Date savedDate, OcUser owner) {
        this.name = name;
        this.step = step;
        this.savedDate = savedDate;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Date getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(Date savedDate) {
        this.savedDate = savedDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OcUser getOwner() {
        return owner;
    }

    public void setOwner(OcUser owner) {
        this.owner = owner;
    }
}
