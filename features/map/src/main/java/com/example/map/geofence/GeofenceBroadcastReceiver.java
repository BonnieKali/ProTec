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

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "myMap";;

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
//                Toast.makeText(context, "Geofence Entered", Toast.LENGTH_SHORT).show();
//                Session.getInstance().generateLiveEvent(EventType.ENTER_HOUSE);
                break;
//            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "GEOFENCE_TRANSITION_EXIT" + transitionType);
//                Toast.makeText(context, "Geofence Exited", Toast.LENGTH_SHORT).show();
                // will tell carers that person has left the house
                boolean left = ((PatientSession) Session.getInstance().getUser()).patientData.locationData.getleftGeofence();
                if (!left) { // notification has already been sent
                    Log.d(TAG,"Geofence Exit: making notification");
                    Session.getInstance().generateLiveEvent(EventType.LEFT_HOUSE);
                    ((PatientSession) Session.getInstance().getUser()).patientData.locationData.LeftGeofence();
                }
                //openMapActivity(context); //BEWARE: Has funky side affects!
                break;
        }
        // ensures patient data is updated correctly about leaving geofence
        ObservableObject.getInstance().updateValue(transitionType);
    }

    private void openMapActivity(Context context){
        Intent i = new Intent(context, MapsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
