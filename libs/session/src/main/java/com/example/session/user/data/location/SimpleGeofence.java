package com.example.session.user.data.location;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

public class SimpleGeofence {
    private final String ID;
    private final LatLng position;
    private final float radius;
    private final int transitionTypes;
    private final int loiteringDelay;
    private final long expirationDuration;

    public SimpleGeofence(String id, LatLng position, float radius, int transitionTypes, int loiteringDelay, long expirationDuration) {
        ID = id;
        this.position = position;
        this.radius = radius;
        this.transitionTypes = transitionTypes;
        this.loiteringDelay = loiteringDelay;
        this.expirationDuration = expirationDuration;
    }

    public Geofence toGeofence(){
        Geofence geofence = new Geofence.Builder()
                .setCircularRegion(position.latitude, position.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(loiteringDelay)    // how long until dwell alert is sent
                .setExpirationDuration(expirationDuration)   // Geofence is always around
                .build();
        return geofence;
    }
}
