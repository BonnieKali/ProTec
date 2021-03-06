package com.example.session.local;

import android.util.Log;

import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.patient.PatientSession;

import java.util.HashMap;
import java.util.HashSet;

/**
 * James Hanratty (s1645821), Van Dimitriou
 * This contains local live data such as all the patientSessions that can be easily accessible
 * to any feature of the App.
 */
public class LocalLiveData {

    private static final String TAG = "LocalLiveData";

    private static boolean initialised = false;
    private static HashMap<String, UserInfo.UserType> userIdMap;
    private static HashSet<PatientSession> allPatients;

    private static void initialiseLocalLiveData(HashMap<String, UserInfo.UserType> idMap, HashSet<PatientSession> allPatients){
        setUserIdMap(idMap);
        setAllPatients(allPatients);
        initialised = true;
    }

    /**
     * Updates the localDB with remoteDB data, it is possible that the two data conflict because local
     * changes have not been pushed to the remoteDB thus we need to merge changes but
     * this is super complicated so instead just let the remote data overwrite this data.
     * @param idMap
     * @param allPatients
     */
    private static void updateAllPatientSessions(HashMap<String, UserInfo.UserType> idMap, HashSet<PatientSession> allPatients){
        // TODO Merge the remoteDB and localDB data instead of just overwritting the localDB data
        setUserIdMap(idMap);
        setAllPatients(allPatients);
    }

    // -- Interface Methods -- //
    /**
     * The public interface that will either initialise the modified data or only update the
     * data from the remote
     * @param idMap
     * @param allPatients
     */
    public static void updateLocalLiveData(HashMap<String, UserInfo.UserType> idMap, HashSet<PatientSession> allPatients){
        Log.d(TAG,"Updating Local live data!");
        Log.d(TAG, "Id Map: " + idMap + "\npatient sessions: " + allPatients);
        if (initialised){
            updateAllPatientSessions(idMap, allPatients);
        }else{
           initialiseLocalLiveData(idMap, allPatients);
        }
    }

    /**
     * Resets the localLiveData such as when somoeone logs out
     */
    public static void resetLocalLiveData(){
        setAllPatients(null);
        setUserIdMap(null);
        initialised = false;
    }

    public static HashMap<String, UserInfo.UserType> retrieveUserIdMap(){
        if (userIdMap == null){
            userIdMap = new HashMap<>();
        }
        return userIdMap;
    }

    public static HashSet<PatientSession> retrieveAllPatients(){
        Log.d(TAG,"All Patient Sessions from " + TAG + " " + allPatients);
        if (allPatients == null){
            allPatients = new HashSet<>();
        }
        return allPatients;
    }

    // -- Setters -- //
    public static void setUserIdMap(HashMap<String, UserInfo.UserType> userIdMaps) {
        userIdMap = userIdMaps;
    }

    public static void setAllPatients(HashSet<PatientSession> allPatient) {
        allPatients = allPatient;
    }

}
