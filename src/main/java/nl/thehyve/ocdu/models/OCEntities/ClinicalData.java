package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.EventType;
import org.openclinica.ws.beans.SiteRefType;
import org.openclinica.ws.beans.StudyRefType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    @Column(columnDefinition = "TEXT")
    private String study;
    @Column(columnDefinition = "TEXT")
    private String site;
    @Column(columnDefinition = "TEXT")
    private String item;
    private String ssid;
    @Column(columnDefinition = "TEXT")
    private String eventName;
    private Integer eventRepeat;
    @Column(columnDefinition = "TEXT")
    private String crfName;


    @Column(columnDefinition = "TEXT")
    private String crfVersion;
    private Integer groupRepeat;

    @Column(columnDefinition = "TEXT")
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
                ", groupRepeat=" + groupRepeat +
                ", value='" + value + '\'' +
                '}';
    }

    /**
     * Tests if a OCItemMapping has a conflicting CRF version. This is required to avoid uploads with similar study,
     * subject, event, event-ordinal and CRF-name but with a different CRF-versions. In such a situation OpenClinica web-services
     * adds 2 CRF's to the event. (TODO add the reference to the OC-bug)
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
        return ((this.study.equals(that.study) &&
                (this.ssid.equals(that.ssid)) &&
                (this.eventName.equals(that.eventName)) &&
                (this.eventRepeat.equals(that.eventRepeat)) &&
                (this.crfName.equals(that.crfName)) &&
                (this.crfVersion.equals(that.crfVersion))));
    }

    /**
     * creates a key value for the ODM generation for each unique chunk of output.
     * @return
     */
    public String createODMKey() {
        StringBuffer ret = new StringBuffer();
        ret.append(ssid);
        ret.append(eventName);
        ret.append(eventRepeat);
        ret.append(crfName);
        ret.append(crfVersion);
        // TODO add the itemGroup level;
        return ret.toString().toUpperCase();
    }

    /**
     * returns <code>true</code> if the event defined in {@param studySubjectWithEventsType} is
     * present present in this ClinicalData.
     * @param studySubjectWithEventsType
     * @return  returns <code>true</code> if the event defined in {@param studySubjectWithEventsType} is
     * present present in this ClinicalData.
     */
    public boolean isEventPresent(StudySubjectWithEventsType studySubjectWithEventsType) {
        return false;
    }

    public List<String> getValues() {
        String[] split = value.split(",");
        List<String> values = Arrays.asList(split);
        return values;
    }
}
