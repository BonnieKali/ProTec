package com.example.services.carer;

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


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This class subclasses the Android Service class. This functionality will run carer-specific
 * processes in the background even when the application shuts down.
 *
 * This contains the Notification logic and listener for carers
 */


public class CarerService extends Service {
    private static final String TAG = "CarerService";

    private Boolean isStarted = false;

    private Context context;
    private Session session;
    private ProTecNotificationsManager proTecNotificationsManager;
    private FallDetectorService fallDetectorService;

    public CarerService() {
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
        if (!isStarted) {
            initService();
            isStarted = true;
        }
    }


    /**
     * Initializes service functions and remote database listener for notifications.
     */
    private void initService() {
        Log.d(TAG, "Carer Service is initializing");

        // Validate Session
        session = Session.getInstance();
        if (!validateSession(session)){
            Log.w(TAG, "Session was not valid. Carer Service is not initializing");
            return;
        }

        // Set remote listener for patient events

        session.setLiveEventListener(taskResult -> {

            // Validate Session
            Session session = Session.getInstance();
            if (!validateSession(session)){
                Log.w(TAG, "Session was not valid. Received event is ignored.");
                return;
            }

            // Get event and validate
            Event event = (Event) taskResult.getData();
            if (event == null) {
                Log.w(TAG, "New event was received but is is null");
                return;
            }

            // Disable event since we received it correctly
            session.disableLiveEvent(event);

            String title = "Patient has";
            String msg = event.patientName;
            if (event.eventType == EventType.FELL) {
                title += " fallen!";
                msg += " has fallen!!! Please check up on him.";
            } else if (event.eventType == EventType.LEFT_HOUSE) {
                title += " left their house!";
                msg += " has left the house!!! Please check up on him";
            }

            proTecNotificationsManager.showSmallNotification(title, msg,
                    Actions.openDashboardIntent(context));

        });
    }


    /**
     * Validates that all session components exist
     *
     * @return Boolean validation result
     */
    private Boolean validateSession(Session session) {
        if (session == null) {
            Log.w(TAG, "validateSession: session is null");
            return false;
        }

        if (!session.isUserSignedIn()) {
            Log.w(TAG, "validateSession: user is signed out");
            return false;
        }

        if (session.getUser() == null) {
            Log.w(TAG, "validateSession: userSession is null");
            return false;
        }

        if (session.getUser().userInfo == null) {
            Log.w(TAG, "validateSession: userInfo is null");
            return false;
        }

        if (session.getUser().userInfo.id == null) {
            Log.w(TAG, "validateSession: userInfo.id is null");
            return false;
        }

        if (session.getUser().userInfo.userType == null) {
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