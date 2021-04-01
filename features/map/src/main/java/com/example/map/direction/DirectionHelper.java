package com.example.map.direction;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * James Hanratty
 * This class contains methods to help with getting the directions
 */
public class DirectionHelper {
    private static final String TAG = "DirectionHelper";
    public static boolean googleMapsOpen = false;

    /**
     * Creates the url string used to ask the directions API for directions
     * @param origin
     * @param dest
     * @param directionMode
     * @param api_key
     * @return: The url used to send to the directions API
     */
    public static String getUrl(LatLng origin, LatLng dest, String directionMode, String api_key) {
        // Origin of route
        String str_origin = String.format("origin=%f,%f",origin.latitude, origin.longitude);
        // Destination of route
        String str_dest = String.format("destination=%f,%f",dest.latitude, dest.longitude);
        // Mode
        String mode = String.format("mode=%s",directionMode);
        // Building the parameters to the web service
        String parameters = String.format("%s&%s&%s",str_origin, str_dest, mode);
        // Output format
        String output = "json";
        // directions web domain
        String domain = String.format("https://maps.googleapis.com/maps/api/directions/");
        // Building the url to the web service
        String url = String.format("%s%s?%s&key=%s",domain,output,parameters,api_key);
        return url;
    }

    /**
     * Get the googleMaps request Intent
     * @param destination
     * @return: The intent used to open google maps
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
