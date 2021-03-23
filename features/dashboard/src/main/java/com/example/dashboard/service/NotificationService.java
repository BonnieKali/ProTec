package com.example.dashboard.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.actions.Actions;
import com.example.notifications.ProTecNotificationsManager;
import com.example.session.Session;
import com.example.session.event.Event;
import com.example.session.event.EventType;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";

    private Boolean isStarted = false;

    private Context context;
    private Session session;
    private ProTecNotificationsManager proTecNotificationsManager;

    public NotificationService() { }

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

        // Set listener for patient events
        session.setLiveEventListener(taskResult -> {
            Event event = (Event) taskResult.getData();

            if (event == null){
                Log.w(TAG, "New event was received but is is null");
                return;
            }

            String title = "Patient";
            String msg = event.patientUid;
            if (event.eventType == EventType.FELL){
                title += " has fallen!";
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