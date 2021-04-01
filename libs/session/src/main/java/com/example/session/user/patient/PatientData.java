package com.example.session.user.patient;

import android.util.Log;

import com.example.session.user.data.biomarker.BiomarkerData;
import com.example.session.user.data.deadreckoning.DRData;
import com.example.session.user.data.deadreckoning.DRDataBuilder;
import com.example.session.user.data.location.LocationData;
import com.example.session.user.UserData;
import com.example.session.user.data.location.LocationDataPatient;
import com.example.session.user.data.relationship.Relationship;
import com.example.session.user.data.relationship.RelationshipPatient;

import java.util.ArrayList;
import java.util.List;

public class PatientData extends UserData {

    // Patient Specific data
    public LocationDataPatient locationData;
    public BiomarkerData biomarkerData;
    public RelationshipPatient relationship;
    public List<DRData> drData;
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
        relationship = new RelationshipPatient();
        drData = new ArrayList<>();

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
    public PatientData(LocationData locationData,
                       BiomarkerData biomarkerData,
                       Relationship relationship,
                       List<DRData> drData){
        super();
        this.locationData = (LocationDataPatient) locationData;
        this.biomarkerData = biomarkerData;
        this.relationship = (RelationshipPatient) relationship;
        this.drData = drData;

        /*
            PLACE MORE OBJECTS HERE
        */

    }

    /**
     * Checks if the carer is assigned to the patient
     * @param carerID
     * @return
     */
    public boolean isAssignedCarer(String carerID){
        Log.d("debug","Checking if patient data contains carer id: " + carerID);
        Log.d("debug","CarerIds: " + relationship.getCarerIDs());
        return relationship.getCarerIDs().contains(carerID);
    }

    @Override
    public String toString() {
        return "PatientData{" +
                ", relationship="+ relationship +
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
