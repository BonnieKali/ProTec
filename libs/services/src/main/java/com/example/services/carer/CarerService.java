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
        if (!isStarted){
            initService();
            isStarted = true;
        }
    }


    /**
     * Initializes service functions and listeners.
     */
    private void initService() {
        Log.d(TAG, "Carer Service is initializing");
        // Set listener for patient events
        if (session == null)
            return;

        session.setLiveEventListener(taskResult -> {
            Event event = (Event) taskResult.getData();
            session.disableLiveEvent(event);

            if (event == null){
                Log.w(TAG, "New event was received but is is null");
                return;
            }
            session.disableLiveEvent(event);

            String title = "Patient has ";
            String msg = event.patientUid;
            if (event.eventType == EventType.FELL){
                title += "fallen!";
                msg += " has fallen!!! Please check up on him.";
            }
            else if (event.eventType == EventType.LEFT_HOUSE){
                title += " has left their house!";
                msg += " has left the house!!! Please check up on him";
            }

            proTecNotificationsManager.showSmallNotification(title, msg,
                    Actions.openDashboardIntent(context));

        });
    }
}