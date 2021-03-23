package com.example.actions;

import android.content.Context;
import android.content.Intent;

public class Actions {

    // Custom IntentFilter ids
    static final String loginStartId = "ProTec.login.open";
    static final String dashboardStartId = "ProTec.dashboard.open";

    static final String notificationServiceId = "ProTec.service.NotificationService";

    /**
     * Creates Intent to go to welcome screen
     *
     * @param context Application context
     * @return Intent object
     */
    public static Intent openLoginIntent(Context context){
        return internalIntent(context, loginStartId);
    }

    /**
     * Creates Intent to go to Dashboard
     *
     * @param context Application context
     * @return Intent object
     */
    public static Intent openDashboardIntent(Context context){
        return internalIntent(context, dashboardStartId);
    }

    /**
     * Creates Intent for notification Service
     *
      * @param context Application context
     * @return Intent object
     */
    public static Intent getNotificationServiceIntent(Context context) {
        return internalIntent(context, notificationServiceId);
    }

        /**
         * Narrows down the IntentFilter search to package-level scope, and returns the new Intent
         * object.
         *
         * @param context Application context
         * @param actionId IntentFilter id
         * @return Intent object
         */
    private static Intent internalIntent(Context context, String actionId){
        return new Intent(actionId).setPackage(context.getPackageName());
    }

}
