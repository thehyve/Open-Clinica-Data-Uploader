package nl.thehyve.ocdu.models.OcDefinitions;

/**
 * Bean to pass on the events which are present in the datafile and which have to be scheduled.
 * Created by Jacob Rousseau on 18-Jul-2016.
 * Immutable
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class EventToSchedule {

    private String studySubjectID;

    private String eventOID;

    private String eventName;

    private String repeatNumber;

    public EventToSchedule(String studySubjectID, String eventOID, String eventName, String repeatNumber) {
        this.studySubjectID = studySubjectID;
        this.eventOID = eventOID;
        this.eventName = eventName;
        this.repeatNumber = repeatNumber;
    }

    public String getStudySubjectID() {
        return studySubjectID;
    }

    public String getEventOID() {
        return eventOID;
    }

    public String getEventName() {
        return eventName;
    }

    public String getRepeatNumber() {
        return repeatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventToSchedule that = (EventToSchedule) o;

        if (!studySubjectID.equals(that.studySubjectID)) return false;
        if (!eventOID.equals(that.eventOID)) return false;
        if (!eventName.equals(that.eventName)) return false;
        return repeatNumber.equals(that.repeatNumber);

    }

    @Override
    public int hashCode() {
        int result = studySubjectID.hashCode();
        result = 31 * result + eventOID.hashCode();
        result = 31 * result + eventName.hashCode();
        result = 31 * result + repeatNumber.hashCode();
        return result;
    }
}
