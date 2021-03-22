package com.example.session.user;


public class UserInfo {

    public String id;
    public String email;

    public enum UserType {
        PATIENT,
        CARER
    }
    public UserType userType;


    /**
     * Class containing all necessary user information
     *
     * @param id Unique user identifier
     * @param email User email
     * @param userType UserType
     */
    public UserInfo(String id, String email, UserType userType){
        this.id = id;
        this.email = email;
        this.userType = userType;
    }


    /**
     * Returns the part of the email to the left of the '@' symbol.
     *
     * @return String username
     */
    public String getUserName(){
        int at_index = email.indexOf("@");

        if (at_index < 1){
            return email;
        }else{
            return email.substring(0, at_index);
        }
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                '}';
    }
}
