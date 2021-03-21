package com.example.protec;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.actions.Actions;
import com.example.session.Session;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Session session = Session.getInstance();

        if (session.isUserSignedIn()){
            // Go directly to dashboard
            startActivity(Actions.openDashboardIntent(this));
        } else {
            // Start app to login
            startActivity(Actions.openLoginIntent(this));
        }
    }
}