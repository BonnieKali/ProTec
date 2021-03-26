package com.example.map.geofence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.map.R;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;

public class Geofence {

    private static final String TAG = String.valueOf(R.string.GeoFence_TAG);
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geofenceHelper;

    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius, Context contex, Activity activity) {
        String id = "test";
        com.google.android.gms.location.Geofence geofence = geofenceHelper.createGeofence(id, latLng, radius, com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.createGeofenceRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.createPendingIntent();
        // check permissions and then add geofence
        if (checkGeofencePermissions(contex, activity)) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Geofence Added...");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMessage = geofenceHelper.getErrorString(e);
                            Log.d(TAG, "onFailure: " + errorMessage);
                        }
                    });
        }
    }

    private boolean checkGeofencePermissions(Context contex, Activity activity){
        boolean fullAccess = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fullAccess = GeoFenceHelper.checkPermissions(contex, activity);
        }
        Log.d(TAG,String.format("Permission Granted: %b",fullAccess));
        return fullAccess;
    }


}
