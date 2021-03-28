package com.example.session.user.data.relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class defines the relationships users have with one
 * another
 */
public class Relationship {

    private HashSet<String> carerIDs;

    public Relationship(HashSet<String> carerIDs){
        this.carerIDs = carerIDs;
    }

    public Relationship(){
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
