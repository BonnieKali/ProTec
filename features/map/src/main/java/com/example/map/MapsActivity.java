package com.example.map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.map.direction.DirectionHelper;
import com.example.map.direction.FetchURL;
import com.example.map.direction.TaskLoadedCallback;
import com.example.map.geofence.GeoFenceHelper;
import com.example.map.location.LocatorHelper;
import com.example.map.map.MapHelper;
import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.map.location.Locator;
import com.example.session.user.patient.PatientSession;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Observable;
import java.util.Observer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        TaskLoadedCallback,
        Observer {

    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 1029;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1039;
    private static final String TAG = "myMap";

    private GoogleMap mMap;

    private GeofencingClient geofencingClient;
    private GeoFenceHelper geofenceHelper;

    private Polyline currentPolyline; // used for displaying path

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
        locator = Locator.getInstance();
        initialiseUser();
    }

    /**
     * Start locating the user by setting up the locator listener and asking/checking
     * for permissions
     */
    private void startLocating(){
        if (checkGeofencePermissions()){
            locator.startLocationUpdates(user, this);
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
        geofenceHelper = new GeoFenceHelper(this, geofencingClient, mMap);
        MapHelper.initialiseMap(mMap);
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        loadGeofences();
        LocatorHelper.loadAndDisplayPatients(user, mMap);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    // -- Actions -- //

    /**
     * If user is a patient then we add a new geofence
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "Long click for " + latLng.toString());
        if (user.getType() == UserInfo.UserType.PATIENT) {
            mMap.clear();
            // set up basic geofence
            String ID = user.getUID();
            if(checkGeofencePermissions()) {
                geofenceHelper.addNewGeofence(ID, latLng, GeoFenceHelper.radius, GeoFenceHelper.transitionType, GeoFenceHelper.loiteringDelay, GeoFenceHelper.expirationDuration);
            }
        }else{
            Toast.makeText(getApplicationContext(), "Only patients may make Geofences",Toast.LENGTH_LONG).show();
        }
    }
    // ----------------

    // -- Permission Checking -- //
    /**
     * Checks if the user has granted the location service needed for them to see themselves on the map
     * @return: true if the neccessary foreground permissions have been accepted
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
     * @return: true if the neccessary background permissions have been accepted
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
     * @return: true if the neccessary permissions to create geofences have been accepted
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
     * Loads the geofences
      */
    private void loadGeofences(){
        geofenceHelper.loadGeofences(user);
    }

    // -- Directions -- //

    /**
     * Opens google maps and returns true if succeeded else false
     * @param destination
     * @return: true if google maps opened successfully
     */
    private boolean openGoogleMaps(LatLng destination){
        Intent mapIntent = DirectionHelper.getGoogleMapsRequestIntent(destination);
        Log.d(TAG,"starting google maps with destination: " + destination);
        try {
            startActivity(mapIntent);
            return true;
        }catch(Exception exception){
//            Toast.makeText(getApplicationContext(), "Google Maps is not installed", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Asks the GoogleMaps Directions API for directions to the destination from the startingPosition
     * @param startingPosition
     * @param destination
     */
    private void getDirections(LatLng startingPosition, LatLng destination){
        Log.d(TAG,"Inside getDirections");
        MarkerOptions place1 = new MarkerOptions().position(new LatLng(startingPosition.latitude, startingPosition.longitude)).title("Starting Location");
        MarkerOptions place2 = new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Destination");
        String directionMode = "walking";
        new FetchURL(this).execute(DirectionHelper.getUrl(place1.getPosition(), place2.getPosition(), directionMode, getString(R.string.google_maps_direction_key)), directionMode);
    }

    /**
     * This is called when a task has been completed such as getting the directions
     * to a destination (Geofence)
     * @param values: The value from the direction API which is the polylineOption
     */
    @Override
    public void onTaskDone(Object... values) {
        Log.d(TAG,"Finished getting directions");
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

    /**
     * This gets called when a geofence event has happened
     * @param observable: The observer object
     * @param o: The object data
     */
    @Override
    public void update(Observable observable, Object o) {
        Log.d(TAG,"MapActivity Update called");
        getDirectionsToGeofence();
    }

    /**
     * This deals with getting directions to the patients geofence
     */
    private void getDirectionsToGeofence(){
        boolean leftGeofence = ((PatientSession) user).patientData.locationData.getleftGeofence();
        boolean googleMapsOpen = DirectionHelper.googleMapsOpen;
        Log.d(TAG,"Patient left geofence: " + leftGeofence);
        Log.d(TAG,"googleMapsOpen: " + googleMapsOpen);
        if (leftGeofence) {
            Log.d(TAG, "getting directions to Geofence...");
            LatLng destination = ((PatientSession) user).patientData.locationData.getAGeofence().getPosition();
            if (!googleMapsOpen) {
                boolean googleMapsOpened = openGoogleMaps(destination);
                if (!googleMapsOpened) {
                    // check if the last known location is available
                    if (locator.isLastKnownLocationAvailable()) {
                        Log.d(TAG, "Last location is known thus manually getting directions");
                        Location startingLocation = locator.getLastLocation();
                        Log.d(TAG, "Starting location: " + startingLocation);
                        LatLng startingLoc = new LatLng(startingLocation.getLatitude(), startingLocation.getLongitude());
                        getDirections(startingLoc, destination);
                    } else {
                        Log.d(TAG, "Last location is NOT known thus not manually getting directions");
                    }
                } else {
                    DirectionHelper.googleMapsOpen = true;
                    Log.d(TAG, "getting directions to Geofence using google maps");
                }
            }
        }
        else{
            // remove the polyline if in the geofence
            if (currentPolyline != null) {
                currentPolyline.remove();
            }
            Log.d(TAG,"Setting google maps false");
            DirectionHelper.googleMapsOpen = false;
        }

    }

    /**
     * Remove this activity as a listener for geofence and location updates
     */
    @Override
    protected void onPause(){
        super.onPause();
        ObservableObject.getInstance().deleteObserver(this);
    }

    /**
     * Add this activity as a listener for geofence and location updates
     */
    @Override
    protected void onResume(){
        super.onResume();
        ObservableObject.getInstance().addObserver(this);
    }
}