package com.example.session.user.data.location;
import android.location.Location;
import com.google.android.gms.location.Geofence;
import java.util.ArrayList;
import java.util.List;

/**
 * James Hanratty
 * This is the patients data class containing all the location information
 */
public class LocationDataPatient extends LocationData{
    private List<SimpleGeofence> geofences;
    private List<Location> locations;

    public LocationDataPatient(List<SimpleGeofence> geofences, List<Location> locations){
        this.geofences = geofences;
        this.locations = locations;
    }
    public LocationDataPatient(){
        this.geofences = new ArrayList<SimpleGeofence>();
        this.locations = new ArrayList<Location>();
    }

    public boolean addSimpleGeofence(SimpleGeofence geofence){
        return geofences.add(geofence);
    }

    public boolean addLocation(Location location){
        return locations.add(location);
    }

    public static LocationDataPatient createDummy(){
        List<SimpleGeofence> geofences = new ArrayList<SimpleGeofence>();
        List<Location> locations = new ArrayList<Location>();
        return new LocationDataPatient(geofences, locations);
    }

}
