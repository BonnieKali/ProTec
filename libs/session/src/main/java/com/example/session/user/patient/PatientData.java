package com.example.session.user.patient;

import com.example.session.data.biomarker.BiomarkerData;
import com.example.session.data.location.LocationData;
import com.example.session.user.UserData;

public class PatientData extends UserData {
    public LocationData locationData;
    public BiomarkerData biomarkerData;

    public PatientData(){
        super();
        locationData = new LocationData();
        biomarkerData = new BiomarkerData();
    }

    public PatientData(LocationData locationData, BiomarkerData biomarkerData){
        super();
        this.locationData = locationData;
        this.biomarkerData = biomarkerData;
    }

    @Override
    public String toString() {
        return "PatientData{" +
                "locationData=" + locationData +
                ", biomarkerData=" + biomarkerData +
                '}';
    }

}
