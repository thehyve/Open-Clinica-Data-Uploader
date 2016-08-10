package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;


/**
 * Represents user-submitted ClinicalData point. Meant to store data as-is.
 * Created by piotrzakrzewski on 16/04/16.
 */
@Entity
public class ClinicalData implements OcEntity, UserSubmitted, EventReference {

    public static final String KEY_SEPARATOR = "\t";


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private UploadSession submission;

    @ManyToOne
    private OcUser owner;
    @Column(columnDefinition = "TEXT")
    private String study;
    @Column(columnDefinition = "TEXT")
    private String site;
    @Column(columnDefinition = "TEXT")
    private String item;

    private String originalItem;

    private String ssid;
    @Column(columnDefinition = "TEXT")
    private String eventName;
    private Integer eventRepeat;
    @Column(columnDefinition = "TEXT")
    private String crfName;

    @Column(columnDefinition = "TEXT")
    private String crfVersion;

    @Column(columnDefinition = "TEXT")
    private String itemGroupOID;

    private Integer groupRepeat;

    @Column(columnDefinition = "TEXT")
    private String value;
    private String studyProtocolName;

    @Override
    public String getStudyProtocolName() {
        return studyProtocolName;
    }

    public void setStudyProtocolName(String studyProtocolName) {
        this.studyProtocolName = studyProtocolName;
    }

    public ClinicalData(String study, String item, String ssid, String eventName, Integer eventRepeat, String crfName, UploadSession submission, String crfVersion, Integer groupRepeat, OcUser owner, String value) {
        this.study = study;
        this.item = item;
        this.ssid = ssid;
        this.eventName = eventName;
        this.eventRepeat = eventRepeat;
        this.crfName = crfName;
        this.submission = submission;
        this.crfVersion = crfVersion;
        this.itemGroupOID = "";
        this.groupRepeat = groupRepeat;
        this.owner = owner;
        this.value = value;
        this.originalItem = item; // TODO: Refactor away this constructor
    }

    public ClinicalData() {
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

    public String getItemGroupOID() {
        return itemGroupOID;
    }

    public void setItemGroupOID(String itemGroupOID) {
        this.itemGroupOID = itemGroupOID;
    }

    public Integer getGroupRepeat() {
        return groupRepeat;
    }


    @Override
    public String toString() {
        return "ClinicalData{" +
                "study='" + study + '\'' +
                ", site='" + site + '\'' +
                ", item='" + item + '\'' +
                ", ssid='" + ssid + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventRepeat=" + eventRepeat +
                ", crfName='" + crfName + '\'' +
                ", crfVersion='" + crfVersion + '\'' +
                ", itemGroupOID='" + itemGroupOID + '\'' +
                ", groupRepeat=" + groupRepeat +
                ", value='" + value + '\'' +
                '}';
    }

    /**
     * Tests if a OCItemMapping has a conflicting CRF version. This is required to avoid uploads with similar study,
     * subject, event, event-ordinal and CRF-name but with a different CRF-versions. In such a situation OpenClinica web-services
     * adds 2 CRF's to the event. (TODO add the reference to the OC-bug)
     *
     * @param that
     * @return <code>true</code> if a conflicting CRF is present.
     */
    public boolean hasSameCRFVersion(ClinicalData that) {
        if ((this.study == null) ||
                (this.ssid == null) ||
                (this.eventName == null) ||
                (this.eventRepeat == null) ||
                (this.crfName == null) ||
                (this.crfVersion == null)) {
            throw new IllegalStateException("Unable to determine conflict state; a field is null");
        }
        if (!(this.study.equals(that.study) &&
                (this.ssid.equals(that.ssid)) &&
                (this.eventName.equals(that.eventName)) &&
                (this.eventRepeat.equals(that.eventRepeat)) &&
                (this.crfName.equals(that.crfName)))) {
            return false;
        }
        return this.crfVersion.equals(that.crfVersion);
    }

    /**
     * Tests if a OCItemMapping has a conflicting CRF version. This is required to avoid uploads with similar study,
     * subject, event, event-ordinal and CRF-name but with a different CRF-versions. In such a situation OpenClinica web-services
     * adds 2 CRF's to the event. (TODO add the reference to the OC-bug)
     *
     * @param that
     * @return <code>true</code> if a conflicting CRF is present.
     */
    public boolean isSameCRF(ClinicalData that) {
        if ((this.study == null) ||
                (this.ssid == null) ||
                (this.eventName == null) ||
                (this.eventRepeat == null) ||
                (this.crfName == null)) {
            throw new IllegalStateException("Unable to determine conflict state; a field is null");
        }
        return (this.study.equals(that.study) &&
                (this.ssid.equals(that.ssid)) &&
                (this.eventName.equals(that.eventName)) &&
                (this.eventRepeat.equals(that.eventRepeat)) &&
                (this.crfName.equals(that.crfName)));
    }

    /**
     * creates a key value for the ODM generation for each unique chunk of output.
     *
     * @return
     */
    public String createODMKey() {
        StringBuffer ret = new StringBuffer();
        ret.append(ssid);
        ret.append(eventName);
        ret.append(eventRepeat);
        ret.append(crfName);
        ret.append(crfVersion);
        ret.append(itemGroupOID);
//        ret.append(groupRepeat.toString());
        return ret.toString().toUpperCase();
    }

    public String createEventRepeatKey() {
        StringBuffer ret = new StringBuffer();
        ret.append(study);
        ret.append(KEY_SEPARATOR);
        ret.append(site);
        ret.append(KEY_SEPARATOR);
        ret.append(ssid);
        ret.append(KEY_SEPARATOR);
        ret.append(eventName);

        return ret.toString().toUpperCase();
    }

    public String createODMGroupingKey() {
        return ssid;
    }

    /**
     * returns <code>true</code> if the event defined in {@param studySubjectWithEventsType} is
     * present present in this ClinicalData.
     *
     * @param studySubjectWithEventsType
     * @return returns <code>true</code> if the event defined in {@param studySubjectWithEventsType} is
     * present present in this ClinicalData.
     */
    public boolean isEventPresent(StudySubjectWithEventsType studySubjectWithEventsType) {
        return false;
    }

    public List<String> getValues() {
        String[] split = value.split(",", -1); // -1 means we will not discard empty values, e.g ,,
        List<String> values = Arrays.asList(split);
        return values;
    }

    public String toOffenderString() {
        String groupRepeatPart;
        if (groupRepeat != null) {
            groupRepeatPart = " item Group Repeat: " + groupRepeat;
        } else {
            groupRepeatPart = " not repeating group ";
        }
        String ofenderMsg = "Subject: " + ssid + " Item: " + item + groupRepeatPart
                + " in CRF: " + crfName + " version: " + crfVersion + " in event: "
                + eventName + " event repeat: " + eventRepeat + " value: "
                + value;
        return ofenderMsg;
    }

    public String getOriginalItem() {
        return originalItem;
    }

    public void setOriginalItem(String originalItem) {
        this.originalItem = originalItem;
    }

}
