package com.example.session.patientNotifications;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This class is responsible for instantiating patient notification objects
 */


public class PatientNotificationBuilder {

    /**
     * Returns an initialized Event
     *
     * @param patientUid Unique patient id
     * @param title Title of notification
     * @param message Message of notification
     * @return PatientNotification object
     */
    public static PatientNotification buildPatientNotification(String patientUid,
                                                               String title,
                                                               String message){

        // Create a timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
        String timestamp = dateFormat.format(new Date()); // Find todays date

        return new PatientNotification(timestamp, patientUid, title, message);

    }
}
