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
        return relationship.removePatient(patientSession.getUID());
    }

    public boolean addPatient(PatientSession patientSession){
        return relationship.addPatient(patientSession.getUID());
    }

    public HashSet<String> getAssignedPatients(){
        return relationship.getPatientIDs();
    }

    /**
     * Checks if the
     * @param ID
     * @return
     */
    public boolean isAssignedPatient(String ID){
        return relationship.getPatientIDs().contains(ID);
    }

    public HashSet<PatientSession> getAssignedPatientSessions(){
        HashSet<String> patientIDs = getAssignedPatients();
        HashSet<PatientSession> allPatientSessions = Session.getInstance().retrieveAllPatientSessions();
        HashSet<PatientSession> patientSessions = new HashSet<>();
        for (PatientSession patient : allPatientSessions){
            if (patientIDs.contains(patient.getUID())){
                patientSessions.add(patient);
            }
        }
        return patientSessions;
    }

    @Override
    public String toString() {
        return "CarerData{" +
                "patients=" + relationship.getPatientIDs() +
                '}';
    }

}
