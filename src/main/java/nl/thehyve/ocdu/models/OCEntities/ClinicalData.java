package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.*;

import javax.persistence.*;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
@Entity
public class ClinicalData implements OcEntity, UserSubmitted, EventReference {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private UploadSession submission;

    @ManyToOne
    private OcUser owner;
    private String study;
    private String site;
    private String item;
    private String ssid;
    private String eventName;
    private Integer eventRepeat;
    private String crfName;


    private String crfVersion;
    private Integer groupRepeat;


    private String value;

    public ClinicalData(String study, String item, String ssid, String eventName, Integer eventRepeat, String crfName, UploadSession submission, String crfVersion, Integer groupRepeat, OcUser owner, String value) {
        this.study = study;
        this.item = item;
        this.ssid = ssid;
        this.eventName = eventName;
        this.eventRepeat = eventRepeat;
        this.crfName = crfName;
        this.submission = submission;
        this.crfVersion = crfVersion;
        this.groupRepeat = groupRepeat;
        this.owner = owner;
        this.value = value;
    }

    protected ClinicalData() {
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setSubmission(UploadSession submission) {
        this.submission = submission;
    }

    @Override
    public void setOwner(OcUser owner) {
        this.owner = owner;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventRepeat(Integer eventRepeat) {
        this.eventRepeat = eventRepeat;
    }

    public void setCrfName(String crfName) {
        this.crfName = crfName;
    }

    public void setCrfVersion(String crfVersion) {
        this.crfVersion = crfVersion;
    }

    public void setGroupRepeat(Integer groupRepeat) {
        this.groupRepeat = groupRepeat;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public OcUser getOwner() {
        return owner;
    }

    public long getId() {
        return id;
    }

    @Override
    public UploadSession getSubmission() {
        return submission;
    }

    @Override
    public String getStudy() {
        return study;
    }

    public String getItem() {
        return item;
    }

    @Override
    public String getSsid() {
        return ssid;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    public Integer getEventRepeat() {
        return eventRepeat;
    }

    public String getCrfName() {
        return crfName;
    }

    public String getCrfVersion() {
        return crfVersion;
    }

    ;

    public Integer getGroupRepeat() {
        return groupRepeat;
    }
}
