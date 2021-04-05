package com.example.services.patient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.actions.Actions;
import com.example.notifications.ProTecNotificationsManager;
import com.example.services.FallDetectorService;
import com.example.session.Session;
import com.example.session.patientNotifications.PatientNotification;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This class subclasses the Android Service class. This functionality will run patient-specific
 * processes in the background even when the application shuts down.
 *
 * This contains the notification logic and listener for patients, along with the fall detection
 * logic.
 */


public class PatientService extends Service {
    private static final String TAG = "PatientService";

    private Boolean isStarted = false;

    private Context context;
    private Session session;
    private ProTecNotificationsManager proTecNotificationsManager;
    private FallDetectorService fallDetectorService;


    public PatientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        session = Session.getInstance();
        proTecNotificationsManager = new ProTecNotificationsManager(context);

        // Start service if not already started
        if (!isStarted){
            initService();
            isStarted = true;
        }
    }

    /**
     * Initializes service functions and listeners.
     */
    private void initService() {
        Log.d(TAG, "Patient Service is initializing");
        // Set listener for patient events
        if (session == null)
            return;

        // Set listener for patient notifications
        session.setLivePatientNotificationListener(taskResult -> {
            PatientNotification pn = (PatientNotification) taskResult.getData();
            Session session = Session.getInstance();

            if (pn == null){
                Log.w(TAG, "New patient notification was received but is is null");
                return;
            }

            if (!session.isUserSignedIn()){
                Log.w(TAG, "New patient notification was received but current user is" +
                        " logged out");
                return;
            }

            // If this notification is meant for the current user_patient then display it
            if (pn.patientUid.equals(session.getUser().userInfo.id)){
                session.disableLivePatientNotification(pn);
                dealWithNotification(pn);
                proTecNotificationsManager.showSmallNotification(
                        pn.title,
                        pn.message,
                        Actions.openDashboardIntent(context));
            }
        });

        if (fallDetectorService == null){
            // Initialize fall detector
            Log.i(TAG, "GETTING FALL DETECTOR SERVICE IN BACKGROUND");
            fallDetectorService = new FallDetectorService(context);       // getting the sensor
        }
    }

    /**
     * Deals with incoming notification and modifies the contents
     *
     * @param pn PatientNotification
     */
    private void dealWithNotification(PatientNotification pn) {
        if (pn.title.equals("REQUEST")){
            pn.title = "Received Request From Carer! ";
            if (pn.message.equals("STATIC")){
                pn.message = "Please perform static biometric test.";
            }else{
                pn.message = "Please perform dynamic biometric test.";
            }
        }

        else if(pn.title.equals("LEFT")){
            pn.title = "Left The House!";
            pn.message = "Please open maps to get directions back home.";
        }

    }

}