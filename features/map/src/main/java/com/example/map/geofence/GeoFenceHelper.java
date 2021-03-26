package com.example.map.geofence;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.map.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * James Hanratty
 * Class that contains methods to help with the GeoFencing
 */
public class GeoFenceHelper extends ContextWrapper {

    private static final String TAG = String.valueOf(R.string.GeoFence_TAG);
    PendingIntent geoPendingIntent;

    public final static int PERMISSION_REQUEST_GEOFENCE_CODE_BACKGROUND = 1000;
    public final static int PERMISSION_REQUEST_GEOFENCE_CODE_NO_BACKGROUND = 1001;

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

    /**
     * checks the Geofence permissions and asks if they are not granted
     * Requires API 25 or above so that permission granting can happen automatically.
     * @param context   The context of the activity that called this method
     * @param activity  The Activity
     * @return          true if all permissions are granted
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermissions(Context context, Activity activity) {
        boolean fullAccess = true;
        // SDK >= 29 requires background location
        if (Build.VERSION.SDK_INT >= 29) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_GEOFENCE_CODE_BACKGROUND);
                fullAccess = false;
            }
        } else {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_GEOFENCE_CODE_NO_BACKGROUND);
                fullAccess = false;
            }
        }
        return fullAccess;
    }

    /**
     * This method determines if the appropriate permissions were granted to use the geofence
     * and will return all true if they were
     * @param requestCode   the request code
     * @param grantResults  the permissions granted or not granted
     * @result permissionsGranted a boolean array where
     * 1st is if permissions were granted and 2nd index is if request code was correct
     */
    public static boolean onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults){
        // first is if permissions were granted, 2nd is if requestcode was correct
        boolean permissionsGranted = false;
        if (requestCode == PERMISSION_REQUEST_GEOFENCE_CODE_BACKGROUND) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted= true;
            } else {
                permissionsGranted= false;
            }
        }else if (requestCode == PERMISSION_REQUEST_GEOFENCE_CODE_NO_BACKGROUND){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
            } else {
                permissionsGranted = false;
            }
        }
        return permissionsGranted;
    }
}

