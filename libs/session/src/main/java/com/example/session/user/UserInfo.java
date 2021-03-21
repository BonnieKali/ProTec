package com.example.session.user;


public class UserInfo {

    public String id;
    public String email;

    public enum UserType {
        PATIENT,
        CARER
    }
    public UserType userType;


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
