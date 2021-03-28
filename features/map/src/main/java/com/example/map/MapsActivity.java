package com.example.map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.map.geofence.GeoFenceHelper;
import com.example.map.map.MapHelper;
import com.example.session.Session;
import com.example.session.remote.RemoteDB;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.UserSessionBuilder;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.data.location.LocationDataCarer;
import com.example.session.user.data.location.SimpleGeofence;
import com.example.map.location.Locator;
import com.example.session.user.data.location.LocationDataPatient;
import com.example.session.user.patient.PatientData;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.example.map.map.MapHelper.initialiseMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 1029;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1039;
    private static final String TAG = "myMap";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geofenceHelper;

    private Locator locator;

    // TODO: Get the patientData // how to get this?
    UserSession user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "Inside maps activity");

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeoFenceHelper(this);
        locator = new Locator(this);
        initialiseUser();
    }

    /**
     * Start locating the user by setting up the locator listener and asking/checking
     * for permissions
     */
    private void startLocating(){
        if (checkGeofencePermissions()){
            locator.startLocationUpdates(user);
        }
    }

    // -- Set Up -- //
    /**
     * Initialises the user to be either patient or carer
     */
    private void initialiseUser(){
        user = ((UserSession) Session.getInstance().getUser());
        if (user.getType() == UserInfo.UserType.PATIENT){
            this.user = (PatientSession) user;
        }else if (user.getType() == UserInfo.UserType.CARER){
            this.user = (CarerSession) user;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        startLocating();    // check permissions
        MapHelper.initialiseMap(mMap);
        GeoFenceHelper.loadGeofences(user, mMap);
    }

    // -- Actions -- //
    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "Long click for " + latLng.toString());
        if (user.getType() == UserInfo.UserType.PATIENT) {
            mMap.clear();
            addGeofence(latLng, 200);
        }else{
            Toast.makeText(getApplicationContext(), "Only patients may make Geofences",Toast.LENGTH_LONG).show();
        }
    }
    // ----------------

    // -- Permission Checking -- //
    /**
     * Checks if the user has granted the location service needed for them to see themselves on the map
     * @return
     */
    private boolean checkForegroundPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return true;
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
        return false;
    }

    /**
     * Checks if the user has granted the location service needed for geofences to work in the background
     * @return
     */
    private boolean checkBackgroundPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return true;
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
        return false;
    }

    /**
     * Checks if all the permissions are needed by individually checking the permissions and asking for them
     * if they are not permitted.
     * @return
     */
    private boolean checkGeofencePermissions(){
        boolean fine_location = checkForegroundPermissions();
        // need background location permission
        if (Build.VERSION.SDK_INT >= 29) {
            boolean background_location = checkBackgroundPermissions();
            if (!background_location){
//                Toast.makeText(getApplicationContext(), "Allow all the time is needed to use Geofences!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        // we will need general location too!
        if (fine_location){
            return true;
        }else {
//            Toast.makeText(getApplicationContext(), "Location is needed!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * This method is called when the user has responded to giving permissions
     * and this method calls the GeoFenceHelper to deal with if they accepted all permissions.
     * If they did then the set location for google maps is enabled
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if SDK < 29 then backjground access is never asked because it is not needed
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                mMap.setMyLocationEnabled(true);
                Toast.makeText(getApplicationContext(), "Geofences enabled!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Allow all the time is needed to use Geofences!", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                mMap.setMyLocationEnabled(true);
                startLocating();    // start locating
                if (!(Build.VERSION.SDK_INT >= 29)) {
                    Toast.makeText(getApplicationContext(), "Geofences enabled!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Location needed!", Toast.LENGTH_LONG).show();
            }
        }
    }
    // -----------------------------

    // -- Geofencing -- //
    /**
     * Adds a geofence to the map by creating a geofence, request and then pending intent
     * @param latLng
     * @param radius
     */
    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "Adding geofence for " + latLng.toString());
        String id = user.userInfo.id;

        com.google.android.gms.location.Geofence geofence = geofenceHelper.createGeofence(id, latLng, radius, com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT);
        SimpleGeofence simpleGeofence = new SimpleGeofence(id, latLng, radius, com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT, 5000, Geofence.NEVER_EXPIRE);
        GeofencingRequest geofencingRequest = geofenceHelper.createGeofenceRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.createPendingIntent();

        // check permissions and then add geofence
        if (checkGeofencePermissions()) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Geofence Added...");
                            Toast.makeText(getApplicationContext(), "added geofence", Toast.LENGTH_SHORT).show();
                            MapHelper.addMarker(mMap, latLng, user.userInfo.getUserName());
                            MapHelper.addCircle(mMap, latLng, 200);
                            if (user.getType() == UserInfo.UserType.PATIENT) {
                                PatientSession patientSession = (PatientSession) user;
                                patientSession.patientData.locationData.addSimpleGeofence(simpleGeofence);
//                                Log.d(TAG, "Success on adding simplegeofence? + ");
                                Session.getInstance().saveState();  // save state with new geofence
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMessage = geofenceHelper.getErrorString(e);
                            String msg = "Please turn on location and make sure it is high accuracy";
                            Log.e(TAG, msg);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}