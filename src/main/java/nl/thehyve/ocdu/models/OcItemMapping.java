package nl.thehyve.ocdu.models;

/**
 * Created by piotrzakrzewski on 07/05/16.
 */
public class OcItemMapping {
    private String study;
    private String eventName;
    private Integer eventOrdinal = new Integer(1);
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

    /**
     * Tests if a OCItemMapping has a conflicting CRF version. This is required to avoid uploads with similar study,
     * subject, event, event-ordinal and CRF-name but with a different CRF-versions. When using OpenClinica web-services
     * 2 CRF are erroneously added to the event. (TODO add the reference to the OC-bug)
     * @param that
     * @return
     */
    public boolean isConflicting(OcItemMapping that) {
        if ((this.study == null) ||
            (this.eventName == null) ||
            (this.eventOrdinal == null) ||
            (crfName == null) ||
            (crfVersion == null)) {
            throw new IllegalStateException("Unable to determine conflict state; a field is null");
        }
        return ((this.study.equals(that.study) &&
                (this.eventName.equals(that.eventName)) &&
                (this.eventOrdinal.equals(that.eventOrdinal)) &&
                (this.crfName.equals(that.crfName)) &&
                (! this.crfVersion.equals(that.crfVersion))));
    }

    @Override
    public String toString() {
        return study+" "+eventName +" "+crfName+" "+crfVersion+" "+ocItemName+" "+usrItemName;
    }
}
