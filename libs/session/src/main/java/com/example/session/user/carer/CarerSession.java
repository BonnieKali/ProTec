package com.example.session.user.carer;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;

public class CarerSession extends UserSession {

    // Carer-specific data
    public CarerData carerData;

    /**
     * Session specific to carer accounts
     *
     * @param userInfo UserInfo object containing information about current user
     * @param carerData
     */
    public CarerSession(UserInfo userInfo, CarerData carerData){
        super(userInfo);
        this.carerData = carerData;
    }

}
