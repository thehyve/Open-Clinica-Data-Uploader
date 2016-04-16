package nl.thehyve.ocdu.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */

@Entity
public class Event {

    private String eventName;
    private String ssid;
    private String study;
    private String location;
    private Date startDate;
    private Date startTime;
    private Date endDate;
    private Date endTime;
    private Integer repeatNumber;
    private String submission;
    private String owner;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public Event(String eventName, String ssid, String study, String location, Date startDate, Date startTime, Date endDate, Date endTime, Integer repeatNumber, String submission, String owner) {
        this.eventName = eventName;
        this.ssid = ssid;
        this.study = study;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.repeatNumber = repeatNumber;
        this.submission = submission;
        this.owner = owner;
    }

    public String getSubmission() {
        return submission;
    }

    public String getOwner() {
        return owner;
    }

    protected Event() {
    }



    public long getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getSsid() {
        return ssid;
    }

    public String getStudy() {
        return study;
    }

    public String getLocation() {
        return location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Integer getRepeatNumber() {
        return repeatNumber;
    }
}
