package com.example.session.user;

import com.example.session.data.biomarker.BiomarkerData;
import com.example.session.data.location.LocationData;


public class UserData {

    // For database purposes
    private String placeHolder;

    // All user data instances are held here
    public LocationData locationData;
    public BiomarkerData biomarkerData;
    /*
        ADD MORE VARIABLES HERE
     */


    /**
     * Default Empty Constructor
     */
    public UserData(){
        this.placeHolder = "data_place_holder";
    }

    /**
     * Constructor
     *
     * @param locationData
     * @param biomarkerData
     */
    public UserData(LocationData locationData,
                    BiomarkerData biomarkerData){
        this();
        this.locationData = locationData;
        this.biomarkerData = biomarkerData;
        /*
            ADD MORE HERE
         */
    }

    @Override
    public String toString() {
        return "UserData{" +
                "locationData=" + locationData +
                ", biomarkerData=" + biomarkerData +
                '}';
    }
}
