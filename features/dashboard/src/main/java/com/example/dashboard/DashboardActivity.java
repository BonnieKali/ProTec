package com.example.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.actions.Actions;
import com.example.dashboard.carer.CarerDashboardFragment;
import com.example.dashboard.patient.PatientDashboardFragment;
import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This activity is the most important one in the application. It sets up the Toolbar and the
 * logout bar. It also holds a fragment container for displaying different types of dashboards.
 * Both Patient and Carer dashboards are contained in this activity.
 */


public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Log.d(TAG, "onCreate");

        // UI components
        TextView title = findViewById(R.id.toolbar_title);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this::navItemSelected);

        // Set support action bar
        setSupportActionBar(findViewById(R.id.patient_dashboard_toolbar));
        getSupportActionBar().setTitle("");

        // Get session instance
        session = Session.getInstance();

        // Display appropriate fragment
        if (session.getUser().getType() == UserInfo.UserType.CARER){
            title.setText("Carer Dashboard");
            showFragment(new CarerDashboardFragment());
        } else {
            title.setText("");
            showFragment(new PatientDashboardFragment());
        }
    }


    /**
     * Displays input fragment inside the activity fragment container.
     *
     * @param fragment Fragment to display
     */
    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    /**
     * Logs out the user when the logout button is pressed
     *
     * @param menuItem
     * @return
     */
    private Boolean navItemSelected(MenuItem menuItem){
        if (menuItem.getItemId() == R.id.action_logout) {
            logout();
        }
        return false;
    }

    /**
     * Removes the current logged_in user session from local storage and goes to the login screen.
     */
    private void logout(){

        session.logOutUser(taskResult -> {
            startActivity(Actions.openLoginIntent(this));
            finish();
        });
    }

    /**
     * Corrects fragment-related pop actions
     */
    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }
}