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
     */
    public static void initialiseMap(GoogleMap mMap) {
        Log.d(TAG, "Initialising Map");
        LatLng edinbuirgh = new LatLng(55.953251, -3.188267);
        mMap.addMarker(new MarkerOptions().position(edinbuirgh).title("Marker in Edinburgh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(edinbuirgh));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    // -- Extra Functionality -- //
    /**
     * Adds a marker to the map
     * @param latLng
     */
    public static Marker addMarker(GoogleMap mMap, LatLng latLng, String title) {
        Log.d(TAG,"Adding marker for " + latLng.toString());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
        return mMap.addMarker(markerOptions);
    }

    /**
     * Adds a circle to the map
     * @param latLng
     * @param r
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
