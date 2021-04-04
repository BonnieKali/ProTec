package com.example.session.user.data.relationship;

import android.util.Log;

import java.util.HashSet;

/**
 * James Hanratty (s1645821)
 * This class defines the relationships Carers can have such as their
 * patients and maybe even friends if we were to integrate a messaging system
 */
public class RelationshipCarer extends Relationship{
    private HashSet<String> patientIDs;

    public RelationshipCarer(HashSet<String> patientIDs){
        this.patientIDs = patientIDs;
    }

    public RelationshipCarer(){
        patientIDs = new HashSet<>();
    }

    public HashSet<String> getPatientIDs(){
        return this.patientIDs;
    }

    public boolean addPatient(String id){
        Log.d(TAG,"Adding Patient " + id);
        return this.patientIDs.add(id);
    }

    public boolean removePatient(String id){
        Log.d(TAG,"Removing Patient " + id);
        return this.patientIDs.remove(id);
    }

    public String toString(){
        return this.patientIDs.toString();
    }
}
