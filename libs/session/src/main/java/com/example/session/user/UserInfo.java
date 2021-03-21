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


    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                '}';
    }
}
