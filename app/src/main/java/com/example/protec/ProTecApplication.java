package com.example.protec;

import android.app.Application;

import com.example.session.Session;
import com.example.threads.BackgroundPool;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This application extends the standard Android application. This code runs before any of the
 * activities are created. Here we initialize the Session Singleton object, and the background
 * thread pool.
 */


public class ProTecApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Here we initialize all library dependencies
        Session.initialize(getApplicationContext());
        BackgroundPool.initialize();
    }

}
