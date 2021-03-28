package com.example.session.user.carer;

import com.example.session.user.UserData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CarerData extends UserData {

    // List containing all patient ids assigned to this carer
    public HashSet<String> patients;

    /**
     * Default Empty Constructor
     */
    public CarerData(){
        super();
        this.patients = new HashSet<>();
    }

    /**
     * Class holding all carer-specific data
     *
     * @param patients
     */
    public CarerData(HashSet<String> patients){
        super();
        this.patients = patients;
    }

    /**
     * Adds a patient Id to the list of patients
     * @param id: ID of the patient
     * @return
     */
    public boolean addPatient(String id){
        return patients.add(id);
    }

    public boolean removePatient(String id){ return patients.remove(id);}

    @Override
    public String toString() {
        return "CarerData{" +
                "patients=" + patients +
                '}';
    }

}
