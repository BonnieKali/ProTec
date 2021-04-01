package com.example.map.map;

import android.graphics.Color;
import android.util.Log;

import com.example.map.geofence.GeoFenceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapHelper {

    private static final String TAG = "myMapHelper";

    /**
     * Makes the map look nice and starting location is edinburgh
     * @param: The google map
     */
    public static void initialiseMap(GoogleMap mMap) {
        Log.d(TAG, "Initialising Map");
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    // -- Extra Functionality -- //
    /**
     * Adds a marker to the map
     * @param: mMap: The google map
     * @param latLng: The markers position
     * @param: The title of the marker
     */
    public static Marker addMarker(GoogleMap mMap, LatLng latLng, String title) {
        Log.d(TAG,"Adding marker for " + latLng.toString());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
        return mMap.addMarker(markerOptions);
    }

    /**
     * Adds a circle to the map
     * @param latLng: The center of the circle
     * @param r: The circle
     */
    public static Circle addCircle(GoogleMap mMap, LatLng latLng, float r) {
        Log.d(TAG,"Adding Circle for " + latLng.toString());
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(r);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        return mMap.addCircle(circleOptions);
    }

}
