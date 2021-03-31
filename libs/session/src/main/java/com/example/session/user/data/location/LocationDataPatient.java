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

    public LocationDataPatient(HashMap<String, SimpleGeofence> geofences, List<myLocation> locations){
        this.geofences = geofences;
        this.locations = locations;
    }
    public LocationDataPatient(){
        this.geofences = new HashMap<String, SimpleGeofence>();
        this.locations = new ArrayList<myLocation>();
    }

    public void addSimpleGeofence(SimpleGeofence geofence){
        Log.d(TAG,"Adding simple geofence: " + geofence);
        geofences.put(geofence.getID(), geofence);
    }

    public boolean addLocation(Location location){
        myLocation my_location = new myLocation(location);
        // check if we should update local database
        Session.getInstance().syncToRemote();
        return locations.add(my_location);
    }

    public static LocationDataPatient createDummy(){
        HashMap<String, SimpleGeofence> geofences = new HashMap<>();
        List<myLocation> locations = new ArrayList<myLocation>();
        return new LocationDataPatient(geofences, locations);
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
        return this.locations.get(this.locations.size() - 1);
    }
    // ----------------

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
