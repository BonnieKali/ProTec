package com.example.session.user.data.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myLocation {

    private LatLng latLng;
    private String dateTime;
    private long lastSeenTime;

    public myLocation(Location location){
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        dateTime = convertTime(location.getTime());
        lastSeenTime = location.getTime();
    }

    public myLocation(LatLng position, String dateTime, long time){
        latLng = position;
        this.dateTime = dateTime;
        lastSeenTime = time;
    }

    /**
     * Converts a Location time into date time
     * @param time
     * @return
     */
    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return format.format(date);
    }

    /**
     * Gets a nice pretty datetime representation of the time
     * @return
     */
    public String getPrettyDateTime(){
        Date date = new Date(lastSeenTime);
        Format format = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        return format.format(date);
    }

    // -- Setters -- //
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }
    // --------------------

    // -- Getters -- //

    public LatLng getLatLng() {
        return latLng;
    }

    public String getDateTime() {
        return dateTime;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
    }

    // ----------------

    public String toString(){
        String str = "myLocation: ("+this.dateTime+") " + this.latLng;
        return str;
    }

}
