package com.example.session.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;
import com.google.gson.Gson;


public class LocalDB {
    private static final String TAG = "LocalDB";

    // Database related constants
    private static final String LOCAL_DB_NAME = "local_db";
    private static final String USER_SESSION_EXISTS_KEY = "user_session_exists";
    private static final String USER_SESSION_TYPE = "user_session_type";
    private static final String USER_SESSION_OBJECT_KEY = "user_session_object";

    private final SharedPreferences sharedPreferences;


    /**
     * Initializes LocalDB
     *
     * @param context Application context
     */
    public LocalDB(Context context){
        // Functional variables
        sharedPreferences = context.getSharedPreferences(LOCAL_DB_NAME, Context.MODE_PRIVATE);
    }


    /**
     * Checks whether there is a saved UserSession in db
     *
     * @return Boolean whether session exists or not
     */
    public Boolean userSessionExists(){
        return sharedPreferences.getBoolean(USER_SESSION_EXISTS_KEY, false);
    }


    /**
     * Deletes the current user session from db. This should be called when the current user
     * chooses to log out.
     */
    public void deleteUserSession(){
        // Set UserSessionExists
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_SESSION_EXISTS_KEY, false);

        // Delete user session
        editor.putString(USER_SESSION_TYPE, "");
        editor.putString(USER_SESSION_OBJECT_KEY, "");

        // Apply changes
        editor.apply();
    }


    /**
     * Retrieves and builds a UserSession object from db.
     *
     * @return UserSession object
     */
    public UserSession getUserSession(){
        Log.d(TAG, "getUserSession: Getting user session");

        // Retrieve UserSession object from storage
        String userSessionString = sharedPreferences.getString(USER_SESSION_OBJECT_KEY,
                "");

        String userTypeString = sharedPreferences.getString(USER_SESSION_TYPE,
                UserInfo.UserType.PATIENT.toString());

        // Build UserSession object from JSON string
        if (userSessionString.equals("")){
            Log.w(TAG, "getUserSession: No user session found in SharedPreferences");
            return null;
        } else{
            Gson gson = new Gson();
            if (userTypeString.equals(UserInfo.UserType.CARER.toString())){
                Log.d(TAG, "getUserSession: Returning Carer Session");
                return gson.fromJson(userSessionString, CarerSession.class);
            }else{
                Log.d(TAG, "getUserSession: Returning PatientSession");
                return gson.fromJson(userSessionString, PatientSession.class);
            }
        }
    }


    /**
     * Saves the UserSession state in db
     *
     * @param userSession UserSession object to save.
     */
    public void saveUserSession(UserSession userSession){
        // Set UserSessionExists
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_SESSION_EXISTS_KEY, true);
        editor.putString(USER_SESSION_TYPE, userSession.getType().toString());

        // Save user session
        Gson gson = new Gson();
        String userSessionString = gson.toJson(userSession, userSession.getClass());
        editor.putString(USER_SESSION_OBJECT_KEY, userSessionString);

        // Apply changes
        editor.apply();
    }


}
