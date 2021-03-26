package com.example.map.geofence;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import com.example.map.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

/**
 * James Hanratty
 * Class that contains methods to help with the GeoFencing
 */
public class GeoFenceHelper extends ContextWrapper {

    private static final String TAG = "myMap";
    PendingIntent geoPendingIntent;

    public GeoFenceHelper(Context base) {
        super(base);
    }

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
    public Geofence createGeofence(String ID, LatLng position, float r, int transitionTypes){
        Geofence geofence = new Geofence.Builder()
                .setCircularRegion(position.latitude, position.longitude, r)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)    // how long until dwell alert is sent
                .setExpirationDuration(Geofence.NEVER_EXPIRE)   // Geofence is always around
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
}

