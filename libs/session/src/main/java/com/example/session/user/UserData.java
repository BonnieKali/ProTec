package com.example.session.user;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This class is the abstract superclass of both PatientData and CarerData.
 */


public abstract class UserData {

    // For database purposes
    private String placeHolder;


    /**
     * Abstract superclass for different UserType data
     */
    public UserData(){
        this.placeHolder = "data_place_holder";
    }


}