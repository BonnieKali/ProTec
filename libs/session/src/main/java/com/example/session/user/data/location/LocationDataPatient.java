package com.example.session.user.data.location;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import java.util.ArrayList;
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

    private HashSet<SimpleGeofence> geofences;
    private List<myLocation> locations;

    public LocationDataPatient(HashSet<SimpleGeofence> geofences, List<myLocation> locations){
        this.geofences = geofences;
        this.locations = locations;
    }
    public LocationDataPatient(){
        this.geofences = new HashSet<SimpleGeofence>();
        this.locations = new ArrayList<myLocation>();
    }



    public boolean addSimpleGeofence(SimpleGeofence geofence){
        Log.d(TAG,"Adding simple geofence: " + geofence);
        return geofences.add(geofence);
    }

    public boolean addLocation(Location location){
        myLocation my_location = new myLocation(location);
        return locations.add(my_location);
    }

    public static LocationDataPatient createDummy(){
        HashSet<SimpleGeofence> geofences = new HashSet<>();
        List<myLocation> locations = new ArrayList<myLocation>();
        return new LocationDataPatient(geofences, locations);
    }

    // -- Getters -- //
    public HashSet<SimpleGeofence> getGeofences() {
        return geofences;
    }

    public List<myLocation> getLocations() {
        return locations;
    }
    // ----------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String toString(){
        String locations = this.locations.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        String geofences = this.geofences.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        String str = "Locations: "+ "\n Geofences:" + geofences;
        return str;
    }

}
