package com.example.session.user.patient;

import android.annotation.SuppressLint;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This class subclasses UserSession and extends it to hold patient-specific logic and data.
 */


@SuppressLint("ParcelCreator")
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
