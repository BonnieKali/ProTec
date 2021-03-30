package com.example.session.patientNotifications;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PatientNotificationBuilder {

    /**
     * Returns an initialized Event
     *
     * @param patientUid
     * @param title
     * @param message
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
