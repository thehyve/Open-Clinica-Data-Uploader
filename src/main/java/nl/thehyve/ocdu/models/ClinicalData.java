package nl.thehyve.ocdu.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
@Entity
public class ClinicalData {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String study;
    private String item;
    private String ssid;
    private String eventName;
    private Integer eventRepeat;
    private String crfName;
    private String submission;
    private String crfVersion;
    private Integer groupRepeat;

    protected ClinicalData() {
    }

    public ClinicalData(String study, String item, String ssid, String eventName, Integer eventRepeat, String crfName, String submission, String crfVersion, Integer groupRepeat) {
        this.study = study;
        this.item = item;
        this.ssid = ssid;
        this.eventName = eventName;
        this.eventRepeat = eventRepeat;
        this.crfName = crfName;
        this.submission = submission;
        this.crfVersion = crfVersion;
        this.groupRepeat = groupRepeat;
    }

    public long getId() {
        return id;
    }

    public String getSubmission() {
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
