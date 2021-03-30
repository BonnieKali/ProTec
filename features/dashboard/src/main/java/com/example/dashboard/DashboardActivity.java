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

        // Get session instance
        session = Session.getInstance();

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


}