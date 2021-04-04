package com.example.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This activity holds a fragment container. Fragments are responsible to perform transitions using
 * a NavigationController.
 */


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}