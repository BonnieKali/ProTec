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
import com.example.session.event.Event;
import com.example.session.event.EventType;
import com.example.session.patientNotifications.PatientNotification;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;

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

            if (pn == null){
                Log.w(TAG, "New patient notification was received but is is null");
                return;
            }

            // If this notification is meant for the current user_patient then display it
            if (pn.patientUid.equals(session.getUser().userInfo.id)){
                proTecNotificationsManager.showSmallNotification(
                        pn.title,
                        pn.message,
                        Actions.openDashboardIntent(context));
            }
        });

        // Initialize fall detector
        Log.i(TAG, "GETTING FALL DETECTOR SERVICE IN BACKGROUND");
        fallDetectorService = new FallDetectorService(context);       // getting the sensor
    }
}