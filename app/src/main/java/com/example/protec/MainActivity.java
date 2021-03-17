package com.example.protec;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.actions.Actions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start app to login
        startActivity(Actions.openLoginIntent(this));
    }
}