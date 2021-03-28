package com.example.session.user.data.relationship;

import java.util.HashSet;

/**
 * James Hanratty
 * This class defines the relationships Patients can have such as their
 * careers and maybe even friends if we were to integrate a messaging system
 */
public class RelationshipPatient extends Relationship{
    private HashSet<String> carerIDs;

    public RelationshipPatient(HashSet<String> carerIDs){
        this.carerIDs = carerIDs;
    }

    public RelationshipPatient(){
        carerIDs = new HashSet<>();
    }

    public HashSet<String> getCarerIDs(){
        return this.carerIDs;
    }

    public boolean addCarer(String id){
        return this.carerIDs.add(id);
    }

    public boolean removeCarer(String id){
        return this.carerIDs.remove(id);
    }

    public String toString(){
        return this.carerIDs.toString();
    }
}
