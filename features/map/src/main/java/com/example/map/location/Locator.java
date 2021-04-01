package com.example.map.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.map.ObservableObject;
import com.example.map.R;
import com.example.map.direction.DirectionHelper;
import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.data.location.LocationDataPatient;
import com.example.session.user.patient.PatientSession;

/**
 * James Hanratty
 * This class is a singleton class that deals with locating the user.
 */
public class Locator {
    private static final String TAG = "myLocator";
    private static final long minTimeBetweenLocationUpdates = 10000;//5*(60*1000);  //5 minutes
    private static final int minDistanceBetweenLocationUpdates = 0;  //10 metres

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    String bestprovider;
    Criteria criteria;

    private static Locator instance = new Locator();

    public static Locator getInstance() {
        return instance;
    }

    private Locator() {
    }
    /**
     * Starts getting location updates
     * @param userSession: The users session, can be carer or patient
     * @param activity: The activity starting the location updates
     */
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(UserSession userSession, Activity activity){
        Log.d(TAG, "Starting Location Updates");
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myLocationListener(userSession);
        bestprovider = locationManager.getBestProvider(getcriteria(), true);
        // need to check permissions
        locationManager.requestLocationUpdates(bestprovider, minTimeBetweenLocationUpdates, minDistanceBetweenLocationUpdates, locationListener);
    }

    /**
     * Checks if the last know location is available
     * @return
     */
    public boolean isLastKnownLocationAvailable(){
        return lastLocation != null;
    }

    /**
     * Stops getting location updates
     */
    public void stopLocationUpdates(){
        Log.d(TAG, "Removing Location Updates");
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Returns the criteria which matches how we want
     * to get location updates
     * @return
     */
    private Criteria getcriteria(){
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    public class myLocationListener implements LocationListener {
        private UserSession user;
        public myLocationListener(UserSession user){
            this.user = user;
        }

        /**
         * This is called when the location has changed.
         * It checks if the user is signed if, if not then cancle locations
         * It also checks if user is patient becasue if not then we dont care about storing location data
         * @param location: The location the user is currently at
         */
        @Override
        public void onLocationChanged(Location location) {
            // stop getting location update if user is signed out.
            if (!Session.getInstance().isUserSignedIn()){
                Log.d(TAG,"User is not signed in: " + user);
                DirectionHelper.googleMapsOpen = false; // reset this to be false when user signs out
                stopLocationUpdates();
                return;
            }
            Log.d("locations","new location: "+location.toString());
            if (location != null){
                // we still know the carers location but we do not save it to the database
                lastLocation = location;
                // add location if user is a patient
                UserSession user = Session.getInstance().getUser();
                if (user != null && user.getType() == UserInfo.UserType.PATIENT) {
                    PatientSession patientSession  = (PatientSession) user;
                    patientSession.patientData.locationData.addLocation(location);
                    ObservableObject.getInstance().updateValue(null);
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    /**
     * Gets the last known location for this user
     * @return: The last known location which can be null so check first
     */
    public Location getLastLocation(){
        Log.d(TAG,"getLastLocation: last location from locator " + this.lastLocation);
        return this.lastLocation;
    }

}
