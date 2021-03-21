package com.example.session.user.patient;

import com.example.session.data.biomarker.BiomarkerData;
import com.example.session.data.location.LocationData;
import com.example.session.user.UserData;

public class PatientData extends UserData {

    // Patient Specific data
    public LocationData locationData;
    public BiomarkerData biomarkerData;

    /*
        PLACE MORE OBJECTS HERE
     */


    /**
     * Default empty constructor
     */
    public PatientData(){
        super();
        locationData = new LocationData();
        biomarkerData = new BiomarkerData();

        /*
            PLACE MORE OBJECTS HERE
        */

    }

    /**
     * Class holding all patient-specific data
     *
     * @param locationData LocationData object
     * @param biomarkerData BiomarkerData object
     */
    public PatientData(LocationData locationData, BiomarkerData biomarkerData){
        super();
        this.locationData = locationData;
        this.biomarkerData = biomarkerData;

        /*
            PLACE MORE OBJECTS HERE
        */

    }

    @Override
    public String toString() {
        return "PatientData{" +
                "locationData=" + locationData +
                ", biomarkerData=" + biomarkerData +
                '}';
    }

}
