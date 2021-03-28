package com.example.session.user.data.relationship;

import java.util.HashSet;

/**
 * James Hanratty
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

    public boolean addCarer(String id){
        return this.patientIDs.add(id);
    }

    public boolean removeCarer(String id){
        return this.patientIDs.remove(id);
    }

    public String toString(){
        return this.patientIDs.toString();
    }
}
