package nl.thehyve.ocdu.models;

import javax.persistence.*;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
@Entity
public class ClinicalData {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private UploadSession submission;
    @ManyToOne
    private OcUser owner;
    private String study;
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

    public String getValue() {
        return value;
    }

    public OcUser getOwner() {
        return owner;
    }

    public long getId() {
        return id;
    }

    public UploadSession getSubmission() {
        return submission;
    }

    public String getStudy() {
        return study;
    }

    public String getItem() {
        return item;
    }

    public String getSsid() {
        return ssid;
    }

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
