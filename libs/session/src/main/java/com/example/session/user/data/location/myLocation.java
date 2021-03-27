package com.example.session.user.data.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myLocation {

    public LatLng latLng;
    public String dateTime;

    public myLocation(Location location){
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        dateTime = convertTime(location.getTime());
    }

    public myLocation(LatLng position, String dateTime){
        latLng = position;
        this.dateTime = dateTime;
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

    public String toString(){
        String str = "myLocation: ("+this.dateTime+") " + this.latLng;
        return str;
    }

}
