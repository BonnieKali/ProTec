package com.example.map.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * James Hanratty (s1645821)
 * This detetmines if the data has been cleared that is required for the maps package to work
 * such as geofences being cleared.
 */
public class DataClearedReceiver extends BroadcastReceiver {

    private static final String TAG = "myDataClearedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // check if intent is for data cleared
        if (Intent.ACTION_PACKAGE_DATA_CLEARED.equals(intent.getAction()) && intent.getData() != null) {
            if (intent.getData().getSchemeSpecificPart() == "com.google.android.gms") {
                // perform action
                Log.w(TAG, "Google PLay services data has been cleared! Need to re-register geofences");
            }
        }
    }
}
