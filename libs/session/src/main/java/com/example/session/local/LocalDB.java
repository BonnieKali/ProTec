package com.example.session.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * Class containing all local database functionality. It uses shared preferences for storage, and
 * serializes all required data to JSON format for saving.
 *
 * It also keeps the user session in storage until they decide to logout.
 */


public class LocalDB {
    private static final String TAG = "LocalDB";

    // Database related constants
    private static final String LOCAL_DB_NAME = "local_db";
    private static final String USER_SESSION_EXISTS_KEY = "user_session_exists";
    private static final String USER_SESSION_TYPE = "user_session_type";
    private static final String USER_SESSION_OBJECT_KEY = "user_session_object";

    // Reference to SharedPreferences of application
    private final SharedPreferences sharedPreferences;


    /**
     * Initializes LocalDB
     *
     * @param context Application context
     */
    public LocalDB(Context context){
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

        // If empty user
        if(!userSession.isInitialised()){
            deleteUserSession();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set UserSessionExists
        editor.putBoolean(USER_SESSION_EXISTS_KEY, true);
        editor.putString(USER_SESSION_TYPE, userSession.getType().toString());

        // Save user session
        Gson gson = new Gson();
        String userSessionString = gson.toJson(userSession, userSession.getClass());
        editor.putString(USER_SESSION_OBJECT_KEY, userSessionString);

        // Apply changes
        editor.apply();
    }

    /**
     * Updates the localDB with remoteDB data, it is possible that the two data conflict because local
     * changes have not been pushed to the remoteDB thus we need to merge changes but
     * this is super complicated so instead just let the remote data overwrite this data.
     * @param idMap
     * @param allPatients
     */
    public void updateAllPatientSessions(HashMap<String, UserInfo.UserType> idMap, HashSet<PatientSession> allPatients){
        Log.d(TAG,"Updating all patient sessions");
        // TODO Merge the remoteDB and localDB data instead of just overwritting the localDB data
        LocalLiveData.updateLocalLiveData(idMap, allPatients);
    }

    public void resetLiveData(){
        LocalLiveData.resetLocalLiveData();}


    /**
     * Retrieves all patients stored in liveData static instance (if none exist locally it will
     * return an empty hashset)
     *
     * @return HashSet with PatientSessions
     * */
    public HashSet<PatientSession> retrieveAllPatientSessions(){
        return LocalLiveData.retrieveAllPatients();
    }

    /**
     * Retrieves all user ids stored in liveData static instance and their associated types
     *
     * @return HashSet with PatientSessions
     * */
    public HashMap<String, UserInfo.UserType> retrieveUserIdTypeMap(){
        return LocalLiveData.retrieveUserIdMap();
    }
    // -----------------------
}
