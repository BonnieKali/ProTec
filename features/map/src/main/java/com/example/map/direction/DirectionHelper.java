package com.example.map.direction;

import com.google.android.gms.maps.model.LatLng;

public class DirectionHelper {
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

}
