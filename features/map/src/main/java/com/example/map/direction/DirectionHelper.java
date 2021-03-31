package com.example.map.direction;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class DirectionHelper {
    private static final String TAG = "DirectionHelper";

    /**
     * Creates the url string used to ask the directions API for directions
     * @param origin
     * @param dest
     * @param directionMode
     * @param api_key
     * @return
     */
    public static String getUrl(LatLng origin, LatLng dest, String directionMode, String api_key) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + api_key;
        return url;
    }

    /**
     * Get the googleMaps request Intent
     * @param destination
     * @return
     */
    public static Intent getGoogleMapsRequestIntent(LatLng destination){
        String dest = String.format("%f,%f",destination.latitude, destination.longitude);
        String mode = "w";
        String name = "google.navigation";
        String request = String.format("%s:q=%s&mode=%s",name, dest, mode);
        Uri gmmIntentUri = Uri.parse(request);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        return mapIntent;
    }

}
