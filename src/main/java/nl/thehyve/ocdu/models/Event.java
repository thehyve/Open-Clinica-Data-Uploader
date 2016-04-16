package nl.thehyve.ocdu.models;

import java.util.Date;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class Event {

    private final String eventName;
    private final String ssid;
    private final String study;

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

    private final String location;
    private final Date startDate;
    private final Date startTime;
    private final Date endDate;
    private final Date endTime;
    private final Integer repeatNumber;

    public Event(String eventName, String ssid, String study, String location, Date startDate, Date startTime, Date endDate, Date endTime, Integer repeatNumber) {
        this.eventName = eventName;
        this.ssid = ssid;
        this.study = study;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.repeatNumber = repeatNumber;
    }
}
