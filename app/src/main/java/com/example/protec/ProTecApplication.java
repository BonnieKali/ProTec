package com.example.protec;

import android.app.Application;

import com.example.session.Session;
import com.example.threads.BackgroundPool;

public class ProTecApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Here we initialize the libraries
        Session.initialize(getApplicationContext());
        BackgroundPool.initialize();
    }

}
