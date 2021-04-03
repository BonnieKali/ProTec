package com.example.session.event;

public class Event {

    public String timestamp;
    public String patientUid;
    public String patientName;
    public EventType eventType;


    /**
     * Event class which contains a patient UID and an EventType
     *
     * @param patientUid Unique user identifier (patient)
     * @param eventType EventType
     */
    protected Event(String timestamp, String patientUid, String patientName, EventType eventType){
        this.timestamp = timestamp;
        this.patientUid = patientUid;
        this.patientName = patientName;
        this.eventType = eventType;
    }


    /**
     * Returns a unique live key for this event. (Only used for live event handling)
     *
     * @return String key
     */
    public String getLiveKey(){
        return eventType + "__"+patientUid;
    }


    @Override
    public String toString() {
        return "Event{" +
                "timestamp='" + timestamp + '\'' +
                ", patientUid='" + patientUid + '\'' +
                ", patientName='" + patientName + '\'' +
                ", eventType=" + eventType +
                '}';
    }
}
