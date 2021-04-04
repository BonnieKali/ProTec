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
 * James Hanratty (s1645821)
 * This is the patients data class containing all their location information
 */
public class LocationDataPatient extends LocationData{
    private static final String TAG = "LocationDataPatient";

    private HashMap<String,SimpleGeofence> geofences;
    private List<myLocation> locations;
    private myLocation lastKnownLocation;
    private boolean leftGeofence;

    public LocationDataPatient(HashMap<String, SimpleGeofence> geofences, List<myLocation> locations,
                               myLocation lastKnownLocation, boolean leftGeofence, boolean goingHome){
        this.geofences = geofences;
        this.locations = locations;
        this.lastKnownLocation = lastKnownLocation;
        Log.d(TAG,"Constructor and left geofence: " + leftGeofence);
        this.leftGeofence = leftGeofence;
    }

    public LocationDataPatient(){
        this.geofences = new HashMap<String, SimpleGeofence>();
        this.locations = new ArrayList<myLocation>();
        lastKnownLocation = null;
        leftGeofence = false;
    }

    /**
     * Adds a simple geofence to the set of geofences
     * @param geofence
     */
    public void addSimpleGeofence(SimpleGeofence geofence){
        Log.d(TAG,"Adding simple geofence: " + geofence);
        geofences.put(geofence.getID(), geofence);
    }

    /**
     * Adds the location of the patient to the last know location
     * and also pushes the new location to the remote database as a
     * myLocation class.
     * @param location: The location of the patient
     */
    public void addLocation(Location location){
        myLocation my_location = new myLocation(location);
        // check if we should update local database
        Session.getInstance().syncToRemote();
        lastKnownLocation = my_location;
        return;
    }

    /**
     * Returns a random geofence for this patient
     * @return
     */
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

    /**
     * Sets that the patient has not left the geofence
     */
    public void returnedToGeofence(){
        this.leftGeofence = false;
    }

    public void LeftGeofence(){
        Log.d(TAG,"left the geofence");
        this.leftGeofence = true;
    }

    @Override
    public String toString(){
        String geofences = this.geofences.toString();
        String str = "Locations: "+ "\n Geofences:" + geofences;
        return str;
    }

}
