package com.example.session.user.data.location;

import java.util.ArrayList;
import java.util.List;


public class LocationData {

    public List<LocationTuple> all_locations;
    public LocationTuple cur_loc;

    /*
        Customize this class
     */


    public LocationData(){
        all_locations = new ArrayList<>();
        cur_loc = new LocationTuple(0L,0L,"");
    }

    public LocationData(List<LocationTuple> all_locations,LocationTuple cur_loc){
        this.all_locations = all_locations;
        this.cur_loc = cur_loc;

    }


}