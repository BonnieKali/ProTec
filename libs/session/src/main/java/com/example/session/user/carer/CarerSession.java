package com.example.session.user.carer;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;

public class CarerSession extends UserSession {

    public CarerData carerData;

    public CarerSession(UserInfo userInfo, CarerData carerData){
        super(userInfo, carerData);
        this.carerData = carerData;
    }

}
