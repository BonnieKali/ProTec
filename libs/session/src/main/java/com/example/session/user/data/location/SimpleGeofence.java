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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleGeofence geofence = (SimpleGeofence) o;
        if (geofence.getID().equals(this.ID)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.ID.hashCode();
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

    // -- Getters -- //
    public LatLng getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public String getID(){
        return this.ID;
    }
    // ----------------

    public String toString(){
        String str = "Geofence: " + this.ID;
        return str;
    }
}
