package nl.thehyve.ocdu.models;

/**
 * Created by piotrzakrzewski on 07/05/16.
 */
public class OcItemMapping {
    private String study;
    private String eventName;
    private String crfName;
    private String crfVersion;
    private String ocItemName;
    private String usrItemName;

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCrfName() {
        return crfName;
    }

    public void setCrfName(String crfName) {
        this.crfName = crfName;
    }

    public String getCrfVersion() {
        return crfVersion;
    }

    public void setCrfVersion(String crfVersion) {
        this.crfVersion = crfVersion;
    }

    public String getOcItemName() {
        return ocItemName;
    }

    public void setOcItemName(String ocItemName) {
        this.ocItemName = ocItemName;
    }

    public String getUsrItemName() {
        return usrItemName;
    }

    public void setUsrItemName(String usrItemName) {
        this.usrItemName = usrItemName;
    }

    @Override
    public String toString() {
        return study+" "+eventName +" "+crfName+" "+crfVersion+" "+ocItemName+" "+usrItemName;
    }
}
