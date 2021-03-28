package com.example.session.user.carer;

import com.example.session.user.UserData;
import com.example.session.user.data.relationship.Relationship;
import com.example.session.user.data.relationship.RelationshipCarer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CarerData extends UserData {

    // List containing all patient ids assigned to this carer
//    public HashSet<String> patients;
    public RelationshipCarer relationship;

    /**
     * Default Empty Constructor
     */
    public CarerData(){
        super();
        this.relationship = new RelationshipCarer();
    }

    /**
     * Class holding all carer-specific data
     *
     * @param relationship
     */
    public CarerData(Relationship relationship){
        super();
        this.relationship = (RelationshipCarer) relationship;
    }

    /**
     * Adds a patient Id to the list of patients
     * @param id: ID of the patient
     * @return
     */
    public boolean addPatient(String id){
        return relationship.getPatientIDs().add(id);
    }

    public boolean removePatient(String id){ return relationship.getPatientIDs().remove(id);}

    @Override
    public String toString() {
        return "CarerData{" +
                "patients=" + relationship.getPatientIDs() +
                '}';
    }

}
