package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.*;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */

@Entity
public class Event implements OcEntity, UserSubmitted, EventReference {

    @Column(columnDefinition = "TEXT")
    private String eventName;

    private String ssid;
    @Column(columnDefinition = "TEXT")
    private String study;
    @Column(columnDefinition = "TEXT")
    private String location;
    private Date startDate;
    private Date startTime;
    private Date endDate;
    private Date endTime;
    private Integer repeatNumber;
    @ManyToOne()
    private UploadSession submission;

    @ManyToOne()
    private OcUser owner;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    public UploadSession getSubmission() {
        return submission;
    }

    @Override
    public void setOwner(OcUser owner) {
        this.owner = owner;
    }

    @Override
    public void setSubmission(UploadSession submission) {
        this.submission = submission;
    }

    @Override
    public OcUser getOwner() {
        return owner;
    }

    protected Event() {
    }


    public long getId() {
        return id;
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

    @Override
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

    @Override
    public String toString() {
        return "Event{" +
                "repeatNumber=" + repeatNumber +
                ", eventName='" + eventName + '\'' +
                ", ssid='" + ssid + '\'' +
                ", study='" + study + '\'' +
                ", location='" + location + '\'' +
                ", startDate=" + startDate +
                ", startTime=" + startTime +
                ", endDate=" + endDate +
                ", endTime=" + endTime +
                '}';
    }
}
