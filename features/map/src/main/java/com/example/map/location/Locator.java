package com.example.map.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.map.R;
import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.data.location.LocationDataPatient;
import com.example.session.user.patient.PatientSession;

public class Locator extends ContextWrapper {
    private static final String TAG = "myMap";

    private LocationManager locationManager;
    private LocationListener locationListener;
    String bestprovider;
    Criteria criteria;

    public Locator(Context base) {
        super(base);
    }

    /**
     * Starts getting location updates
     * @param patientData
     */
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(UserSession patientData){
        Log.d(TAG, "Starting Location Updates");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myLocationListener(patientData);
        bestprovider = locationManager.getBestProvider(getcriteria(), true);
        // need to check permissions
        locationManager.requestLocationUpdates(bestprovider, 0, 0, locationListener);
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

        @Override
        public void onLocationChanged(Location location) {
            // stop getting location update if user is signed out.
            if (!Session.getInstance().isUserSignedIn()){
                Log.d(TAG,"User is not signed in: " + user);
                stopLocationUpdates();
                return;
            }
            Log.d("locations","new location: "+location.toString());
            if (location != null){
                double tlat = location.getLatitude();
                double tlong = location.getLongitude();

                // add location if user is a patient
                UserSession user = Session.getInstance().getUser();
//                Log.d(TAG,"User is null: " + (user==null));
                if (user != null && user.getType() == UserInfo.UserType.PATIENT) {
                    PatientSession patientSession  = (PatientSession) user;
                    patientSession.patientData.locationData.addLocation(location);
//                    Log.d(TAG,"User is not null: " + user);
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

}
