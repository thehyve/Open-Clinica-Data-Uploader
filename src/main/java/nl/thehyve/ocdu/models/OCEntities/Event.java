package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.EventType;
import org.openclinica.ws.beans.SiteRefType;
import org.openclinica.ws.beans.StudyRefType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Map;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */

@Entity
public class Event implements OcEntity, UserSubmitted, EventReference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private UploadSession submission;
    @ManyToOne
    private OcUser owner;
    private String eventName;
    private String ssid;
    private String study;
    private String location;
    private String site;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String repeatNumber;

    @Override
    public UploadSession getSubmission() {
        return submission;
    }

    @Override
    public void setSubmission(UploadSession submission) {
        this.submission = submission;
    }

    @Override
    public OcUser getOwner() {
        return owner;
    }

    @Override
    public void setOwner(OcUser owner) {
        this.owner = owner;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRepeatNumber() {
        return repeatNumber;
    }

    public void setRepeatNumber(String repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    @Override
    public String toString() {
        return "Event{" +
                "repeatNumber=" + repeatNumber +
                ", eventName='" + eventName + '\'' +
                ", ssid='" + ssid + '\'' +
                ", study='" + study + '\'' +
                ", location='" + location + '\'' +
                ", site='" + site + '\'' +
                ", startDate=" + startDate +
                ", startTime=" + startTime +
                ", endDate=" + endDate +
                ", endTime=" + endTime +
                '}';
    }

    /**
     * creates a key to filter a list for all events present in a list of {@link ClinicalData}.
     * @return a key uniquely identifying an event
     */
    public String createEventKey(String eventOID) {
        StringBuffer ret = new StringBuffer();
        ret.append(study);
        ret.append(site);
        ret.append(ssid);
        ret.append(eventOID);
        ret.append(repeatNumber);
        return ret.toString().toUpperCase();
    }


    public EventType createEventType(Map<String, String> eventNameOIDMap) {
        EventType ret = new EventType();
        StudyRefType studyRefType = new StudyRefType();
        studyRefType.setIdentifier(study);
        SiteRefType siteRefType = new SiteRefType();
        siteRefType.setIdentifier(site);
        studyRefType.setSiteRef(siteRefType);
        ret.setStudyRef(studyRefType);
        String eventOID = eventNameOIDMap.get(eventName);
        if (StringUtils.isEmpty(eventOID)) {
            throw new IllegalStateException("No eventOID found for the event with name " + eventName);
        }
        ret.setEventDefinitionOID(eventName);
        return ret;
    }
}
