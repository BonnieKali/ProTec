package com.example.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.actions.Actions;
import com.example.dashboard.carer.CarerDashboardFragment;
import com.example.dashboard.patient.PatientDashboardFragment;
import com.example.map.location.Locator;
import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    private Session session;

    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 1029;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1039;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Log.d(TAG, "onCreate");

        // UI components
        TextView title = findViewById(R.id.toolbar_title);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this::navItemSelected);

        // Get session instance
        session = Session.getInstance();
        checkGeofencePermissions();

        // Display appropriate fragment
        if (session.getUser().getType() == UserInfo.UserType.CARER){
            title.setText("Carer Dashboard");
            showFragment(new CarerDashboardFragment());
        } else {
            title.setText("Patient Dashboard");
            showFragment(new PatientDashboardFragment());
        }
    }


    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }


    private Boolean navItemSelected(MenuItem menuItem){
        if (menuItem.getItemId() == R.id.action_logout) {
            logout();
        }
        return false;
    }

    private void logout(){

        session.logOutUser(taskResult -> {
            startActivity(Actions.openLoginIntent(this));
        });
    }

    private boolean checkForegroundPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
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

    // -- Permission Checking -- //
    /**
     * Start locating the user by setting up the locator listener and asking/checking
     * for permissions
     */
    private void startLocating(){
        if (checkGeofencePermissions()){
            Locator.getInstance().startLocationUpdates(session.getUser(), this);
        }
    }

    /**
     * Checks if the user has granted the location service needed for geofences to work in the background
     * @return
     */
    private boolean checkBackgroundPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
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
//                mMap.setMyLocationEnabled(true);
                Toast.makeText(getApplicationContext(), "Geofences enabled!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Allow all the time is needed to use Geofences!", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
//                mMap.setMyLocationEnabled(true);
                startLocating();    // start locating
                if (!(Build.VERSION.SDK_INT >= 29)) {
                    Toast.makeText(getApplicationContext(), "Geofences enabled!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Location needed!", Toast.LENGTH_LONG).show();
            }
        }
    }
}