package com.example.map.geofence;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.map.R;
import com.example.map.map.MapHelper;
import com.example.session.Session;
import com.example.session.remote.RemoteDB;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.data.location.SimpleGeofence;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableProcess;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;

import static com.example.map.map.MapHelper.addCircle;

/**
 * James Hanratty
 * Class that contains methods to help with the GeoFencing
 */
public class GeoFenceHelper extends ContextWrapper {

    private static final String TAG = "myGeofenceHelper";
    public static final int transitionType = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
    public static final int loiteringDelay = 4000;
    public static final long expirationDuration = Geofence.NEVER_EXPIRE;
    public static final float radius = 200; // a large radius because location data can be inaccurate

    private PendingIntent geoPendingIntent;
    private GeofencingClient geofencingClient;
    private GoogleMap mMap;

    public GeoFenceHelper(Context base,GeofencingClient geofencingClient, GoogleMap mMap) {
        super(base);
        this.geofencingClient = geofencingClient;
        this.mMap = mMap;
    }

    // -- Create Geofences -- //
    /**
     * Creates the geofencing request which detemines when the geofence alert
     * is created
     * @param geofence: The built Geofence
     * @return: The geofence request
     */
    public GeofencingRequest createGeofenceRequest(Geofence geofence){
        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        return request;
    }

    /**
     * Creates the actual Geofence
     * @param ID: Unique ID of the geofence
     * @param position: The origin position
     * @param r: The radius of the cirlce
     * @param transitionTypes: The allow transisitons for alerts
     * @return: The geofence object
     */
    public static Geofence createGeofence(String ID, LatLng position, float r, int transitionTypes, int loiteringDelay, long expirationDuration){
        Geofence geofence = new Geofence.Builder()
                .setCircularRegion(position.latitude, position.longitude, r)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(loiteringDelay)    // how long until dwell alert is sent
                .setExpirationDuration(expirationDuration)   // Geofence is always around
                .build();
        return geofence;
    }

    /**
     * Creates or returns a pending intent for the geofence
     * @return
     */
    public PendingIntent createPendingIntent() {
        // this suggests there is only one pending intent for all geofences? remove this and create
        // a new one for everyone
        if (geoPendingIntent != null) {
            return geoPendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geoPendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return geoPendingIntent;
    }

    /**
     * Get the error string for the geofence error
     * @param e
     * @return
     */
    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }
    // -------------------------

    // -- Load and show Geofences -- //
    /**
     * Shows the geofences but does not create them since they already exist?
     * Well need to actually check they exist
     */
    public void loadGeofences(UserSession user){
        Log.d(TAG,"Loading Geofences");
        // get the patients geofence
        if (user.getType() == UserInfo.UserType.PATIENT){
            HashMap<String, SimpleGeofence> existingSimpleGeofences = ((PatientSession) user).patientData.locationData.getGeofences();
            for (SimpleGeofence geofence: existingSimpleGeofences.values()){
                addExisitingGeofence(geofence);
            }
            // Get the carers geofences which are those of their patients
        }else if (user.getType() == UserInfo.UserType.CARER){
            // background process to get patients from carers
            RunnableTask get_patients = () ->
                    new TaskResult<HashSet>(getPatientsForCarer((CarerSession) user));

            // method called when get_patients has completeds
            OnTaskCompleteCallback callback = taskResult -> {
                showGeofences((HashSet<PatientSession>) taskResult.getData(), mMap);
            };
            // run the task
            BackgroundPool.attachTask(get_patients, callback);
        }
    }

    /**
     * This is the method the background task runs which retrieves the
     * patients of the carer
     * @param user
     * @return
     */
    private static HashSet<PatientSession> getPatientsForCarer(CarerSession user) {
        Log.d(TAG,"Getting patients for carer: " + user.userInfo.getUserName());
        HashSet<PatientSession> patients = user.carerData.getAssignedPatientSessions();
        return patients;
    }

    /**
     * Is the callback fro showing the Geofences of patients
     * @param patients
     */
    private static void showGeofences(HashSet<PatientSession> patients, GoogleMap mMap){
        for (PatientSession patient : patients) {
            for (String ID : patient.patientData.locationData.getGeofences().keySet()) {
                SimpleGeofence geofence = patient.patientData.locationData.getGeofences().get(ID);
                Log.d(TAG,"Showing Geofence" + geofence + "\nfor patient: " + patient.userInfo.getUserName());
                showGeofence(geofence, patient.userInfo.getUserName(), mMap);
            }
        }
    }

    /**
     * Shows a single geofence
     * @param geofence
     */
    private static void showGeofence(SimpleGeofence geofence, String title, GoogleMap mMap){
        LatLng position = geofence.getPosition();
        float radius = geofence.getRadius();
        MapHelper.addMarker(mMap, position, title);
        MapHelper.addCircle(mMap, position, radius);
    }

    /**
     * Adds a new geofence by passing all the needed attributes to create it
     * @param id
     * @param position
     * @param radius
     * @param transitionTypes
     * @param loiteringDelay
     * @param expirationDuration
     */
    @SuppressLint("MissingPermission")
    public void addNewGeofence(String id, LatLng position, float radius, int transitionTypes, int loiteringDelay, long expirationDuration){
        Geofence geofence = createGeofence(id, position, radius, transitionTypes, loiteringDelay, expirationDuration);
        SimpleGeofence simpleGeofence = new SimpleGeofence(id, position, radius, transitionTypes, loiteringDelay, expirationDuration);
        createLiveGeofence(geofence, simpleGeofence);
    }

    public void addExisitingGeofence(SimpleGeofence simpleGeofence){
        Geofence geofence = simpleGeofence.toGeofence();
        createLiveGeofence(geofence, simpleGeofence);
    }

    /**
     * Creates the actual live geofence that is stored inside the android system
     * @param geofence
     * @param simpleGeofence
     */
    @SuppressLint("MissingPermission")
    private void createLiveGeofence(Geofence geofence, SimpleGeofence simpleGeofence){
        GeofencingRequest geofencingRequest = createGeofenceRequest(geofence);
        PendingIntent pendingIntent = createPendingIntent();
        UserSession user = Session.getInstance().getUser();
        LatLng position = simpleGeofence.getPosition();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                        Toast.makeText(getApplicationContext(), "added geofence", Toast.LENGTH_SHORT).show();
                        MapHelper.addMarker(mMap, position, user.userInfo.getUserName());
                        MapHelper.addCircle(mMap, position, 200);
                        if (user.getType() == UserInfo.UserType.PATIENT) {
                            PatientSession patientSession = (PatientSession) user;
                            patientSession.patientData.locationData.addSimpleGeofence(simpleGeofence);
                            Log.d(TAG, "Success on adding simplegeofence? + ");
                            Session.getInstance().saveState();  // save state with new geofence
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = getErrorString(e);
                        String msg = "Please turn on location and make sure it is high accuracy";
                        Log.e(TAG, msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

