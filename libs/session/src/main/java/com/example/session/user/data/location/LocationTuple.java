package com.example.session.user.data.location;


public class LocationTuple {

    public Long lat;
    public Long lon;
    public String timestamp;

    public LocationTuple(Long lat, Long lon, String timestamp){
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
    }

}
