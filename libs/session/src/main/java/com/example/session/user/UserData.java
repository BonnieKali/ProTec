package com.example.session.user;


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
