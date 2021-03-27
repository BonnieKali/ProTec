package com.example.session.user.patient;

import com.example.session.user.data.biomarker.BiomarkerData;
import com.example.session.user.data.location.LocationData;
import com.example.session.user.UserData;
import com.example.session.user.data.location.LocationDataPatient;

import java.util.ArrayList;

public class PatientData extends UserData {

    // Patient Specific data
    public LocationDataPatient locationData;
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
        biomarkerData = initializeBiomarkerData();

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
        this.locationData = (LocationDataPatient) locationData;
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


    /***
     * Initialize and returns a BiomarkerData object
     * @return initilzied BiomarkerData
     */
    public BiomarkerData initializeBiomarkerData(){
        ArrayList<Double> accuracy_static_list = new ArrayList<Double>();
        ArrayList<Double> accuracy_dynamic_list = new ArrayList<Double>();
        ArrayList<Double> speed_static_list = new ArrayList<Double>();
        ArrayList<Double> speed_dynamic_list = new ArrayList<Double>();
        return new BiomarkerData(accuracy_static_list, accuracy_dynamic_list, speed_static_list, speed_dynamic_list);
    }

}
