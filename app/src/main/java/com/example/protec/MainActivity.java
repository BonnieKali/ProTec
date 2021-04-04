package com.example.protec;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.actions.Actions;
import com.example.session.Session;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This is the first activity spawned by the application. Its task is to check whether the user
 * is already signed in. If the user is signed in, they go directly to their corresponding
 * dashboard. Otherwise they go to the login page
 */


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