package com.example.session;

import android.content.Context;
import android.util.Log;

import com.example.session.event.Event;
import com.example.session.event.EventBuilder;
import com.example.session.event.EventType;
import com.example.session.local.LocalDB;
import com.example.session.local.LocalLiveData;
import com.example.session.patientNotifications.PatientNotification;
import com.example.session.patientNotifications.PatientNotificationBuilder;
import com.example.session.remote.Authentication;
import com.example.session.remote.RemoteDB;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.UserSessionBuilder;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This class contains all the Session class methods logic for decoupling purposes. (It basically
 * contains all the same methods, such that function names are decoupled between back-end and
 * front-end.
 */


public class SessionHandler {
    private static final String TAG = "SessionHandler";

    // Databases for data storage
    public LocalDB localDB;
    public RemoteDB remoteDB;

    // Authenticates users from remote Auth firebase portal
    public Authentication authentication;

    // Keeps all local user session information
    public UserSession userSession;
    public HashSet<UserSession> userSessions;


    /**
     * Class that implements the details of all public Session calls
     */
    public SessionHandler(Context context){
        Log.d(TAG, "Initializing");
        authentication = new Authentication();

        localDB = new LocalDB(context);
        remoteDB = new RemoteDB();

        userSession = UserSessionBuilder.fromLocal(localDB);
    }



    //-------------|
    // Data access |
    //-------------|

    /**
     * Updates the local data from remote with blocking
     */
    public void updateLocalDataFromRemote(){
        updateLocalData();
    }

    /**
     * Updates the local Data with remote Database data and calls uiCallback
     * @param uiCallback
     */
    public void updateLocalDataFromRemote(OnTaskCompleteCallback uiCallback){
        // Log out user in background thread
        RunnableTask task = () -> {
            // Save any unsaved changes to remote database
            updateLocalData();
            return new TaskResult<>(false);
        };
        BackgroundPool.attachTask(task, uiCallback);
    }

    /**
     * Updates the localData with remote data
     */
    private void updateLocalData(){
        Log.d(TAG,"Updating local data...");
        // Get user id map from remote, and save to localDB
        HashMap<String, UserInfo.UserType> allUserTypes = remoteDB.getAllUsers();
        Log.d("debug","All user types" + allUserTypes);
        // Get all patient objects from remote, and save to localDB
        HashMap<String, PatientSession> allPatientIDSessions = remoteDB.getAllPatients();
        Log.d("debug","All patient session IDs" + allPatientIDSessions);
        HashSet<PatientSession> allPatientSessions  = new HashSet<PatientSession>( allPatientIDSessions.values() );
        Log.d("debug","All patient sessions from remote data: " + allPatientSessions);
        // now update locallivedata
        localDB.updateAllPatientSessions(allUserTypes, allPatientSessions);
    }

    /**
     * Updates the value of the input settings key for the specified user
     *
     * @param uid Unique user id
     * @param settingKey Settings key to update
     * @param settingValue New key value
     */
    public void setPatientSetting(String uid, String settingKey, Object settingValue){
        remoteDB.setPatientSetting(uid, settingKey, settingValue);
    }


     // -- retrieve data

    public HashSet<PatientSession> retrieveAllPatientSessions() {
        return localDB.retrieveAllPatientSessions();
    }

    public HashMap<String, UserInfo.UserType> retrieveUserIdTypeMap(){
        return localDB.retrieveUserIdTypeMap();
    }


    //------------------|
    // Patient Settings |
    //------------------|

    /**
     * Retrieves all patient settings for a given patient and calls ui Callback with TaskResult
     * (HashMap(setting_id -> setting_object))
     *
     * @param uid Patient unique id
     * @param uiCallback callback executed on UI thread
     */
    public void getPatientSettings(String uid, OnTaskCompleteCallback uiCallback){
        RunnableTask task = () -> {
            Map<String, Object> res = remoteDB.getPatientSettings(uid);
            return new TaskResult<>(res);
        };
        BackgroundPool.attachTask(task, uiCallback);
    }

    /**
     * Sets a listener to the firebase database for the given patients settings. The given callback
     * is called when there is any change to the settings of the patient.
     *
     * @param uid patient id
     * @param callback callback to be called with the value as input
     */
    public void setPatientSettingsListener(String uid, OnTaskCompleteCallback callback){
        remoteDB.setPatientSettingsListener(uid, callback);
    }



    //-----------------------|
    // State synchronization |
    //-----------------------|

    /**
     * Saves the current state of the session to local database in a background thread.
     */
    public void saveState() {
        BackgroundPool.attachProcess(() -> localDB.saveUserSession(userSession));
    }

    /**
     * Saves the current state of the session to local database in current thread
     * (blocking statement).
     */
    public void saveStateBlock(){
        localDB.saveUserSession(userSession);
    }

    /**
     * Saves all user data to the remote database in a background thread.
     */
    public void syncToRemote() {
        BackgroundPool.attachProcess(() -> remoteDB.updateUser(userSession.getUID(), userSession));
    }

    /**
     * Saves all user data to the remote database in current thread (blocking statement).
     */
    public void syncToRemoteBlock(){
        remoteDB.updateUser(userSession.getUID(), userSession);
    }



    //---------------------|
    // User Authentication |
    //---------------------|

    /**
     * Specifies whether there is a currently signed in user.
     *
     * @return isUserSignedIn
     */
    public Boolean isUserSignedIn() {
        return userSession.isInitialised();
    }

    /**
     * Logs out current user by deleting the session
     */
    public void logOutUser(OnTaskCompleteCallback uiCallback){

        // Log out user in background thread
        RunnableTask task = () -> {
            // Save any unsaved changes to remote database
            syncToRemoteBlock();
            localDB.resetLiveData();    // clear the live data

            // Reset local session variables
            authentication.logOut();
            userSession = UserSessionBuilder.buildEmpty();

            // Save the new non-logged-in state
            saveStateBlock();

            return new TaskResult<>(true);
        };

        BackgroundPool.attachTask(task, uiCallback);
    }

    /**
     * Attempts to authenticate user with given credentials. If the operation is successful, a
     * new UserSession object is built from the remote database and stored inside the global
     * session instance.
     *
     * @param email User email
     * @param password User password
     * @param uiCallback Callback to run on UI thread. The result of the operation is passed as
     *                   argument to the uiCallback.
     */
    public void signInUserWithEmailPassword(String email, String password,
                                            OnTaskCompleteCallback uiCallback) {

        // Create a background task which completes all authentication steps in the background.
        // Sign in user, get authentication task result and if successful, pull user details from
        // remote database and constructs a new UserSession.
        RunnableTask authenticationTask = () -> {
            TaskResult<String> userResult =
                    authentication.signInUserWithEmailPassword(email, password);

            // If there was an error in the authentication return it directly to the UI
            if (userResult instanceof TaskResult.Error){
                return userResult;
            }

            //If authentication was successful, pull stored User from remote database
            String userID = userResult.getData();
            try {
                this.userSession = UserSessionBuilder.fromRemote(remoteDB, userID);
            } catch (UserSessionBuilder.UserNotFoundInRemoteException e){
                return new TaskResult.Error<>(e);
            }

            // Save UserSession locally
            this.saveState();

            return new TaskResult<>(true);
        };

        // Send task to the background thread pool and run the input callback on the UI thread
        BackgroundPool.attachTask(authenticationTask, uiCallback);
    }

    /**
     * Creates a new user with the given credentials and signs him in in a background thread. The
     * result is passed to the input uiCallback as a TaskResult argument.
     *
     * @param email User email
     * @param password User password
     * @param uiCallback OnTaskCompleteCallback to be executed on UI thread.
     */
    public void createUserWithEmailPassword(String email, String password,
                                            UserInfo.UserType userType,
                                            OnTaskCompleteCallback uiCallback) {
        RunnableTask authenticationTask = () -> {
            TaskResult<String> userResult =
                    authentication.createUserWithEmailPassword(email, password);

            // If there was an error in the authentication return it directly to the UI
            if (userResult instanceof TaskResult.Error){
                return userResult;
            }

            // Build new UserSession and save it locally/remotely
            String userID = userResult.getData();

            this.userSession = UserSessionBuilder.buildNew(userID, email, userType);
            Log.d(TAG, "createUserWithEmailPassword: New user was created!");
            Log.d(TAG, "createUserWithEmailPassword: "+userSession.toString());

            // Save UserSession locally
            this.saveState();

            // Save new User to remote database
            this.syncToRemote();

            return new TaskResult<>(true);
        };

        // Send task to the background thread pool and run the input callback on the UI thread
        BackgroundPool.attachTask(authenticationTask, uiCallback);
    }



    //----------------|
    // EVENT HANDLING |
    //----------------|

    /**
     * Sets a listener for new database live events. This can only be used by CarerSessions.
     *
     * @param onTaskCompleteCallback callback to be executed in the UI thread when a new event
     *                               arrives. This callback will provide the TaskResult(event) as
     *                               an argument.
     * @return Boolean operation successful
     */
    public Boolean setLiveEventListener(OnTaskCompleteCallback onTaskCompleteCallback){
        // If the current user is a patient, do not set the listener
        if (userSession.userInfo.userType == UserInfo.UserType.PATIENT) {
            Log.w(TAG, "setLiveEventListener: Current user is a patient. No listener added");
            return false;
        }

        remoteDB.setNewLiveEventListener(onTaskCompleteCallback);
        return true;
    }

    /**
     * Creates an Event object and sends it to the remote database with a generation request.
     * It then returns this event object
     *
     * @param eventType Type of event to generate
     * @return Event object
     */
    public Event generateLiveEvent(EventType eventType){
        // If the current user is a carer, do not generate event
        if (userSession.userInfo.userType == UserInfo.UserType.CARER){
            return null;
        }

        Event event = EventBuilder.buildEvent((PatientSession) userSession ,eventType);
        remoteDB.generateEvent(event);
        return event;
    }

    /**
     * Removes the given event from the live_events in the database. This should be called when
     * the event has been dealt with.
     *
     * @param event Event object
     */
    public void disableLiveEvent(Event event){
        remoteDB.disableEvent(event);
    }



    //-----------------------|
    // PATIENT NOTIFICATIONS |
    //-----------------------|

    /**
     * Sends a patient notification listener for new live_patient_notifications
     *
     * @param callback callback to be executed in the UI thread when a new notification
     *                 arrives. This callback will provide the TaskResult(PatientNotification) as
     *                 an argument.
     * @return Boolean success
     */
    public Boolean setLivePatientNotificationListener(OnTaskCompleteCallback callback){
        // If the current user is a patient, do not set the listener
        if (userSession.userInfo.userType == UserInfo.UserType.CARER) {
            Log.w(TAG, "setLivePatientNotificationListener: Current user is a carer. No listener added");
            return false;
        }

        remoteDB.setNewLivePatientNotificationListener(callback);
        return true;
    }

    /**
     * Generates a live patient notification.
     *
     * @param patientUid
     * @param title
     * @param msg
     * @return
     */
    public PatientNotification generateLivePatientNotification(String patientUid,
                                                               String title,
                                                               String msg){
        // Check if current user is patient and if so do not generate
//        if(userSession.userInfo.userType == UserInfo.UserType.PATIENT){
//            return null;
//        }

        PatientNotification patientNotification = PatientNotificationBuilder.
                buildPatientNotification(patientUid, title, msg);

        remoteDB.generatePatientNotification(patientNotification);
        return patientNotification;
    }

    /**
     * Disables the live notification pointed by the input object (removes it from
     * live_patient_notifications)
     *
     * @param patientNotification PatientNotification object
     */
    public void disableLivePatientNotification(PatientNotification patientNotification){
        remoteDB.disablePatientNotification(patientNotification);
    }
}
