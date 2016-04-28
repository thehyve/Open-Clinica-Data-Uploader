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

    @ManyToOne
    private OcUser owner;

    public enum Step {
        MAPPING, PATIENTS, EVENTS, OVERVIEW
    }

    private Step step;
    private Date savedDate;

    public UploadSession(String name, Step step, Date savedDate) {
        this.name = name;
        this.step = step;
        this.savedDate = savedDate;
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
}
