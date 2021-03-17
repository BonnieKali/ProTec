package com.example.actions;

import android.content.Context;
import android.content.Intent;

public class Actions {

    static final String loginStartId = "ProTec.login.open";
    static final String dashboardStartId = "ProTec.dashboard.open";


    public static Intent openLoginIntent(Context context){
        return internalIntent(context, loginStartId);
    }

    public static Intent openDashboardIntent(Context context){
        return internalIntent(context, dashboardStartId);
    }

    private static Intent internalIntent(Context context, String actionId){
        return new Intent(actionId).setPackage(context.getPackageName());
    }

}
