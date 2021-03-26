package com.example.session.user.patient;

import com.example.session.user.data.biomarker.BiomarkerData;
import com.example.session.user.data.location.LocationData;
import com.example.session.user.UserData;
import com.example.session.user.data.location.LocationDataPatient;

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
        locationData = new LocationDataPatient();
        biomarkerData = new BiomarkerData(0);

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
