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

        // Validate Session
        session = Session.getInstance();
        if (!validateSession(session)){
            Log.w(TAG, "Session was not valid. Service is not initializing.");
            return;
        }

        // Set listener for patient notifications
        session.setLivePatientNotificationListener(taskResult -> {

            // Validate Session
            Session session = Session.getInstance();
            if (!validateSession(session)){
                Log.w(TAG, "Session was not valid. Received notification is ignored.");
                return;
            }

            // Get notification
            PatientNotification pn = (PatientNotification) taskResult.getData();
            if (pn == null){
                Log.w(TAG, "New patient notification was received but is is null");
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


    /**
     * Validates that all session components exist
     * @return Boolean validation result
     */
    private Boolean validateSession(Session session){
        if (session == null){
            Log.w(TAG, "validateSession: session is null");
            return false;
        }

        if (!session.isUserSignedIn()){
            Log.w(TAG, "validateSession: user is signed out");
            return false;
        }

        if (session.getUser() == null){
            Log.w(TAG, "validateSession: userSession is null");
            return false;
        }

        if (session.getUser().userInfo == null){
            Log.w(TAG, "validateSession: userInfo is null");
            return false;
        }

        if (session.getUser().userInfo.id == null){
            Log.w(TAG, "validateSession: userInfo.id is null");
            return false;
        }

        if (session.getUser().userInfo.userType == null){
            Log.w(TAG, "validateSession: userInfo.userType is null");
            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
    }
}