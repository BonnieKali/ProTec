package com.example.session.user.patient;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;

public class PatientSession extends UserSession {

    public PatientData patientData;


    /**
     * Patient-specific session
     * @param userInfo UserInfo object containing all user information
     * @param patientData PatientData object containing all patient-specific data
     */
    public PatientSession(UserInfo userInfo, PatientData patientData){
        super(userInfo);
        this.patientData = patientData;
    }

    @Override
    public String toString(){
        String a = "Name: " + this.userInfo.getUserName() + " ";
        return a + patientData.toString();
    }

}
