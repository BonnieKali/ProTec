package com.example.session.local;

import android.util.Log;

import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.patient.PatientSession;

import java.util.HashMap;
import java.util.HashSet;

public class LocalLiveData {

    private static final String TAG = "LocalLiveData";

    private static boolean initialised = false;
    private static HashMap<String, UserInfo.UserType> userIdMap;
    private static HashSet<PatientSession> allPatients;
    private static HashSet<PatientSession> modifiedPatients;
    private static HashSet<PatientSession> carerPatientSessions;
//    private UserSession userSession;

    private static void initialiseLocalLiveData(HashMap<String, UserInfo.UserType> idMap, HashSet<PatientSession> allPatients){
        setUserIdMap(idMap);
        setAllPatients(allPatients);
        setModifiedPatients(new HashSet<PatientSession>());
        setCarerPatientSessions(allPatients);
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
        setCarerPatientSessions(allPatients);
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
        setModifiedPatients(null);
        setAllPatients(null);
        setUserIdMap(null);
        setCarerPatientSessions(null);
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

    public static HashSet<PatientSession> retrieveModifiedPatients(){
        if (modifiedPatients == null){
            modifiedPatients = new HashSet<>();
        }
        return modifiedPatients;
    }

    public static HashSet<PatientSession> retrieveCarerPatientSessions(){
        return carerPatientSessions;
    }

    public static Boolean addModifiedPatient(PatientSession patientSession){
        return retrieveModifiedPatients().add(patientSession);
    }

    // -- Setters -- //
    public static void setUserIdMap(HashMap<String, UserInfo.UserType> userIdMaps) {
        userIdMap = userIdMaps;
    }

    public static void setAllPatients(HashSet<PatientSession> allPatient) {
        allPatients = allPatient;
    }

    public static void setModifiedPatients(HashSet<PatientSession> modifiedPatient) {
        modifiedPatients = modifiedPatient;
    }

    /**
     * Adds the patients to a list of patients belonging to the carer
     * @param allPatientSessions
     */
    private static void setCarerPatientSessions(HashSet<PatientSession> allPatientSessions){
        Log.d(TAG,"Setting Patients that belong to carer");
        String ID = Session.getInstance().getUser().getUID();
        carerPatientSessions = new HashSet<>();
        if (allPatientSessions == null){
            Log.d(TAG,"allPatientSessions == null");
            return;
        }else {
            for (PatientSession patient : allPatientSessions) {
                boolean isCarer = isBelongToCarer(patient);
                if (isCarer) {
                    carerPatientSessions.add(patient);
                }
            }
        }
        Log.d(TAG,"All patients belonging to carer: " + carerPatientSessions);
    }

    /**
     * Checks if the patient belongs to the carer
     * @param patient
     * @return
     */
    private static boolean isBelongToCarer(PatientSession patient) {
        String id = Session.getInstance().getUser().getUID();
        for (String carerID : patient.patientData.relationship.getCarerIDs()) {
            if (id.equals(carerID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean removePatientFromCarer(PatientSession patientSession) {
        return carerPatientSessions.remove(patientSession);
    }

    public static boolean addPatientFromCarer(PatientSession patientSession) {
        return carerPatientSessions.add(patientSession);
    }
}
