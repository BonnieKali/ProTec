package com.example.protec;

import android.app.Application;

import com.example.threads.BackgroundPool;

public class ProTecApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BackgroundPool.initialize();
    }

}
