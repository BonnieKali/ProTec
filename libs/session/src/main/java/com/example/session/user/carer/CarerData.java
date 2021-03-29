package com.example.session.user.carer;

import com.example.session.Session;
import com.example.session.user.UserData;
import com.example.session.user.data.relationship.Relationship;
import com.example.session.user.data.relationship.RelationshipCarer;
import com.example.session.user.patient.PatientSession;

import java.util.ArrayList;
import java.util.HashMap;
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

    public boolean removePatient(PatientSession patientSession){
        relationship.removePatient(patientSession.getUID());
        return Session.getInstance().removePatientFromCarer(patientSession);
    }

    public boolean addPatient(PatientSession patientSession){
        relationship.addPatient(patientSession.getUID());
        return Session.getInstance().addPatientFromCarer(patientSession);
    }

    public HashSet<PatientSession> getAssignedPatients(){
        return Session.getInstance().retrieveCarerPatientSessions();
    }

    @Override
    public String toString() {
        return "CarerData{" +
                "patients=" + relationship.getPatientIDs() +
                '}';
    }

}
