package com.example.services.patient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import com.example.actions.Actions;
import com.example.notifications.ProTecNotificationsManager;
import com.example.services.FallDetectorService;
import com.example.services.R;
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
//        showAlertDialog();
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

        // initialise geofence detection

    }

    private void showAlertDialog() {

        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
        kl.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
        wakeLock.acquire();


        final CharSequence[] items = { "String", "String", "String", "String" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Title");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("String")) {
                    dialog.dismiss();

                } else if (items[item].equals("String")) {
                    dialog.dismiss();

                } else if (items[item].equals("String")) {

                }else if (items[item].equals("String")) {
                    dialog.dismiss();


                }
            }
        });

        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        alert.show();

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {

            }
        });


        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });


    }


}