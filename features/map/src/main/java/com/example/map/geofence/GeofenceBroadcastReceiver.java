package com.example.map.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.map.MapsActivity;
import com.example.map.ObservableObject;
import com.example.map.R;
import com.example.session.Session;
import com.example.session.event.EventType;
import com.example.session.user.patient.PatientSession;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * James Hanratty (s1645821)
 * Class that is called whenever a gefoence transition event happens
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastRec";;

    /**
     * This method is called when a user interacts with 1 or more geofences
     * and calls the processGeofenceEvent method to deal with them.
     * send.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Recieved intent");
        processGeofenceEvent(context, intent);
    }

    /**
     * Deals with the processGeofenceEvent by choosing which notifications to send.
     * @param context
     * @param intent
     */
    public void processGeofenceEvent(Context context, Intent intent){
        if (!Session.getInstance().isUserSignedIn()){
            return;
        }
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Error receiving geofence event");
            return;
        }

        // get a list of triggered geofences
        List<Geofence> triggeringGeofences  = geofencingEvent.getTriggeringGeofences();

        // it is possible a user has interacted with multiple geofences
        for (Geofence geofence: triggeringGeofences ) {
            Log.d(TAG, "Recieved gefoence request for ID: " + geofence.getRequestId());
        }

        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        Log.d(TAG, "Location: " + location);
        Log.d(TAG, "Transition Type: " + transitionType);



        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "GEOFENCE_TRANSITION_ENTER" + transitionType);
                // user has returned
                ((PatientSession) Session.getInstance().getUser()).patientData.locationData.returnedToGeofence();
                break;
//            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "GEOFENCE_TRANSITION_EXIT" + transitionType);
                // will tell carers that person has left the house
                boolean left = ((PatientSession) Session.getInstance().getUser()).patientData.locationData.getleftGeofence();
                Log.d(TAG,"Already left: " + left);
                if (!left) { // notification has already been sent
                    Log.d(TAG,"Geofence Exit: making notification");
                    Session session = Session.getInstance();
                    session.generateLiveEvent(EventType.LEFT_HOUSE);
                    session.generateLivePatientNotification(
                            session.getUser().getUID(),
                            "LEFT",
                            "PLEASE_BACK_HOME");
                    ((PatientSession) Session.getInstance().getUser()).patientData.locationData.LeftGeofence();
                }
                //openMapActivity(context); //BEWARE: Has funky side affects!
                break;
        }
        // ensures patient data is updated correctly about leaving geofence
        ObservableObject.getInstance().updateValue(transitionType);
    }

    /**
     * Opens the MapActivity screen
     * @param context
     */
    private void openMapActivity(Context context){
        Intent i = new Intent(context, MapsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
