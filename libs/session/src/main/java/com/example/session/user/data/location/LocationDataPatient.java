package com.example.session.user.data.location;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.example.session.Session;
import com.google.android.gms.location.Geofence;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;

/**
 * James Hanratty
 * This is the patients data class containing all the location information
 */
public class LocationDataPatient extends LocationData{
    private static final String TAG = "LocationDataPatient";

    private HashMap<String,SimpleGeofence> geofences;
    private List<myLocation> locations;
    private myLocation lastKnownLocation;
    private boolean leftGeofence;

    public LocationDataPatient(HashMap<String, SimpleGeofence> geofences, List<myLocation> locations, myLocation lastKnownLocation, boolean leftGeofence){
        this.geofences = geofences;
        this.locations = locations;
        this.lastKnownLocation = lastKnownLocation;
        this.leftGeofence = leftGeofence;
    }
    public LocationDataPatient(){
        this.geofences = new HashMap<String, SimpleGeofence>();
        this.locations = new ArrayList<myLocation>();
        lastKnownLocation = null;
        leftGeofence = false;
    }

    public void addSimpleGeofence(SimpleGeofence geofence){
        Log.d(TAG,"Adding simple geofence: " + geofence);
        geofences.put(geofence.getID(), geofence);
    }

    public void addLocation(Location location){
        myLocation my_location = new myLocation(location);
        // check if we should update local database
        Session.getInstance().syncToRemote();
        lastKnownLocation = my_location;
        return;
    }

    public SimpleGeofence getAGeofence(){
        return (SimpleGeofence) geofences.values().toArray()[0];
    }

    // -- Getters -- //
    public HashMap<String, SimpleGeofence> getGeofences() {
        return geofences;
    }

    public List<myLocation> getLocations() {
        return locations;
    }

    public myLocation getLastKnownLocation(){
        return this.lastKnownLocation;
    }

    public boolean getleftGeofence(){
        return this.leftGeofence;
    }
    // ----------------

    public void returnedToGeofence(){
        this.leftGeofence = false;
    }

    public void LeftGeofence(){
        this.leftGeofence = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String toString(){
        String locations = this.locations.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        String geofences = this.geofences.toString();

        String str = "Locations: "+ "\n Geofences:" + geofences;
        return str;
    }

}
