package nl.thehyve.ocdu.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
        MAPPING, FEEDBACK_DATA, SUBJECTS, FEEDBACK_SUBJECTS, EVENTS, FEEDBACK_EVENTS, UPLOAD_ODM, PRE_UPLOAD_OVERVIEW, UPLOAD_SETTINGS,  OVERVIEW, FINAL
    }

    private Step step;
    private CRFStatusAfterUpload crfStatusAfterUpload = CRFStatusAfterUpload.DATA_ENTRY_COMPLETED;
    private Date savedDate;
    private String study;

    private boolean uponNotStarted = true;
    private boolean uponDataEntryStarted;
    private boolean uponDataEntryCompleted;


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

    public CRFStatusAfterUpload getCrfStatusAfterUpload() {
        return crfStatusAfterUpload;
    }

    public void setCrfStatusAfterUpload(CRFStatusAfterUpload crfStatusAfterUpload) {
        this.crfStatusAfterUpload = crfStatusAfterUpload;
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

    public boolean isUponNotStarted() {
        return uponNotStarted;
    }

    public void setUponNotStarted(boolean uponNotStarted) {
        this.uponNotStarted = uponNotStarted;
    }

    public boolean isUponDataEntryStarted() {
        return uponDataEntryStarted;
    }

    public void setUponDataEntryStarted(boolean uponDataEntryStarted) {
        this.uponDataEntryStarted = uponDataEntryStarted;
    }

    public boolean isUponDataEntryCompleted() {
        return uponDataEntryCompleted;
    }

    public void setUponDataEntryCompleted(boolean uponDataEntryCompleted) {
        this.uponDataEntryCompleted = uponDataEntryCompleted;
    }

    public void setOwner(OcUser owner) {
        this.owner = owner;
    }



}
