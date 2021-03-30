package com.example.actions;

import android.content.Context;
import android.content.Intent;

public class Actions {

    // Custom IntentFilter ids
    static final String loginStartId = "ProTec.login.open";
    static final String dashboardStartId = "ProTec.dashboard.open";
    static final String bioTestId = "ProTec.bioTest.open";
    static final String mapOpenId = "ProTec.map.open";

    static final String carerServiceId = "ProTec.service.CarerService";
    static final String patientServiceId = "ProTec.service.PatientService";

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
     * Creates Intent to go to go to bio Test view
     *
     * @param context Application context
     * @return Intent object
     */
    public static Intent openBioTestIntent(Context context){
        return internalIntent(context, bioTestId);
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
     * Creates Intent to go to map
     *
     * @param context Application context
     * @return Intent object
     */
    public static Intent openMapIntent(Context context){
        return internalIntent(context, mapOpenId);
    }

    /**
     * Creates Intent for background Service
     *
     * @param context Application context
     * @return Intent object
     */
    public static Intent getCarerServiceIntent(Context context) {
        return internalIntent(context, carerServiceId);
    }

    /**
     * Creates Intent for background Service
     *
     * @param context Application context
     * @return Intent object
     */
    public static Intent getPatientServiceIntent(Context context) {
        return internalIntent(context, patientServiceId);
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
