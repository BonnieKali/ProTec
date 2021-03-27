package com.example.protec;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.actions.Actions;
import com.example.session.Session;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get any previously started user session
        Session session = Session.getInstance();

        // Check if a user is signed in
        if (session.isUserSignedIn()){
            // Go directly to dashboard
            startActivity(Actions.openDashboardIntent(this));
        } else {
            // Go to login
            startActivity(Actions.openLoginIntent(this));
        }
        finish();
    }
}