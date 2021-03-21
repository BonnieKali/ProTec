package com.example.session.user.patient;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;

public class PatientSession extends UserSession {

    public PatientData patientData;

    public PatientSession(UserInfo userInfo, PatientData patientData){
        super(userInfo, patientData);
        this.patientData = patientData;
    }

}
