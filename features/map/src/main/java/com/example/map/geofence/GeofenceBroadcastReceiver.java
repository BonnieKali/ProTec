package com.example.map.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.map.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
//import com.example.map.Geohelper;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = String.valueOf(R.string.GeoFence_TAG);

    /**
     * This method is called when a user interacts with 1 or more geofences
     * and calls the processGeofenceEvent method to deal with them.
     * send.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
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
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                break;
        }
    }
}
