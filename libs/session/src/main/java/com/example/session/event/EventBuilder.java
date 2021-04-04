package com.example.session.event;

import com.example.session.user.patient.PatientSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This class is responsible for instantiating event objects.
 */


public class EventBuilder {

    /**
     * Returns an initialized Event
     *
     * @param patientSession patient user session which caused this event
     * @param eventType Type of event the patient initialized
     * @return Event object
     */
    public static Event buildEvent(PatientSession patientSession, EventType eventType){
        // Get user id
        String uid = patientSession.getUID();
        String patientName = patientSession.userInfo.getUserName();

        // Create a timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
        String timestamp = dateFormat.format(new Date()); // Find todays date

        return new Event(timestamp, uid, patientName, eventType);

    }

}
