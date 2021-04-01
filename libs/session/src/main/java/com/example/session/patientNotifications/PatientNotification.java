package com.example.session.patientNotifications;

public class PatientNotification {

    public String timestamp;
    public String patientUid;
    public String title;
    public String message;


    /**
     * PatientNotification class that holds notification information. There can only be one
     * notification per title, patientUid pair.
     *
     * @param patientUid unique patient id
     * @param title notification title
     * @param message notification message
     */
    public PatientNotification(String timestamp, String patientUid, String title, String message){
        this.timestamp = timestamp;
        this.patientUid = patientUid;
        this.title = title;
        this.message = message;
    }

    /**
     * Returns a unique key for this notification. The notification key is composed of:
     * [title]__[patientUid]
     *
     * @return String unique key
     */
    public String getLiveKey(){
        return title + "__" + patientUid;
    }

    @Override
    public String toString() {
        return "PatientNotification{" +
                "timestamp='" + timestamp + '\'' +
                ", patientUid='" + patientUid + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
