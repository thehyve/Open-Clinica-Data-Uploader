package nl.thehyve.ocdu.models;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class ClinicalData {

    private final String study;
    private final String item;
    private final String ssid;
    private final String eventName;
    private final Integer eventRepeat;
    private final String crfName;

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

    public Integer getGroupRepeat() {
        return groupRepeat;
    }

    private final String crfVersion;
    private final Integer groupRepeat;


    public ClinicalData(String study, String item, String ssid, String eventName, Integer eventRepeat, String crfName, String crfVersion, Integer groupRepeat) {
        this.study = study;
        this.item = item;
        this.ssid = ssid;
        this.eventName = eventName;
        this.eventRepeat = eventRepeat;
        this.crfName = crfName;
        this.crfVersion = crfVersion;
        this.groupRepeat = groupRepeat;
    }
}
