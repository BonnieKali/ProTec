package com.example.session;

import android.content.Context;
import android.util.Log;

import com.example.session.event.Event;
import com.example.session.event.EventBuilder;
import com.example.session.event.EventType;
import com.example.session.local.LocalLiveData;
import com.example.session.patientNotifications.PatientNotification;
import com.example.session.patientNotifications.PatientNotificationBuilder;
import com.example.session.remote.RemoteDB;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class Session {
    private static final String TAG = "Session";

    // Reference to application context
    private static Context appContext;

    // Static singleton Session instance
    private static Session instance;

    // Session utils
    private final SessionHandler sessionHandler;



    //-------------------------|
    // Singleton functionality |
    //-------------------------|

    /**
     * Called on application creation to set a static app context and initialize the Session
     * instance.
     *
     * @param context Application context
     */
    public static void initialize(Context context){
        Log.d(TAG, "Initializing");
        appContext = context;
//        getInstance();  // Initialize the instance object with the initialize call
    }

    /**
     * Singleton Session class responsible for initializing all persistent session components.
     * Private constructor only called once, implementing singleton pattern.
     *
     * @param context application Context
     */
    private Session(Context context){
        sessionHandler = new SessionHandler(context);
    }

    /**
     * Returns the singleton Session instance. This is a thread-safe call
     *
     * @return Session singleton instance
     */
    public static synchronized Session getInstance(){
        if(instance == null) {
            instance = new Session(appContext);
        }
        return instance;
    }



    //--------------------|
    // UserSession Access |
    //--------------------|

    /**
     * Returns the active UserSession object
     *
     * @return UserSession object
     */
    public UserSession getUser(){
        return sessionHandler.userSession;
    }



    //-------------|
    // Data Access |
    //-------------|

    /**
     * Updates the local data from remote with blocking
     */
    public void updateLocalDataFromRemote(){
        sessionHandler.updateLocalDataFromRemote();
    }

    /**
     * Updates the local Data with remote Database data and calls uiCallback
     * @param uiCallback
     */
    public void updateLocalDataFromRemote(OnTaskCompleteCallback uiCallback){
        Log.d(TAG,"updating local data with remote data");
        sessionHandler.updateLocalDataFromRemote(uiCallback);
    }

    /**
     * Updates the value of the input settings key for the specified user.
     *
     * @param uid Unique user id
     * @param settingKey Settings key to update
     * @param settingValue New key value. This can be:
     *                      <ul>
     *                      <li>Boolean
     *                      <li>Long
     *                      <li>Double
     *                      <li>String
     *                      <li>Map&lt;String, Object&gt;
     *                      <li>List&lt;Object&gt;
     *                      </ul>
     */
    public void setPatientSetting(String uid, String settingKey, Object settingValue){
        sessionHandler.setPatientSetting(uid, settingKey, settingValue);
    }

    // -- retrieve data

    public HashSet<PatientSession> retrieveAllPatientSessions() {
        return sessionHandler.retrieveAllPatientSessions();
    }

    public HashMap<String, UserInfo.UserType> retrieveUserIdTypeMap(){
        return sessionHandler.retrieveUserIdTypeMap();
    }

    /**
     * Retrieves all patient settings for a given patient and calls ui Callback with TaskResult
     * (HashMap(setting_id -> setting_object))
     *
     * @param uid Patient unique id
     * @param uiCallback callback executed on UI thread
     */
    public void getPatientSettings(String uid, OnTaskCompleteCallback uiCallback){
        sessionHandler.getPatientSettings(uid, uiCallback);
    }

    // ------------



    //-----------------------|
    // State synchronization |
    //-----------------------|

    /**
     * Saves the current state of the session to local database in a background thread.
     */
    public void saveState(){
        sessionHandler.saveState();
    }

    /**
     * Saves all user data to the remote database in a background thread.
     */
    public void syncToRemote(){
        sessionHandler.syncToRemote();
    }



    //---------------------|
    // User Authentication |
    //---------------------|

    /**
     * Specifies whether there is a currently signed in user.
     *
     * @return isUserSignedIn
     */
    public Boolean isUserSignedIn(){
        return sessionHandler.isUserSignedIn();
    }

    /**
     * Logs out current user by deleting the session
     */
    public void logOutUser(OnTaskCompleteCallback uiCallback){
        sessionHandler.logOutUser(uiCallback);
    }

    /**
     * Signs in user with given credentials in a background thread. The result is passed to the
     * input uiCallback as a TaskResult argument.
     *
     * If the operation is successful, a new UserSession object is built from the remote database
     * and stored inside the global session instance.
     *
     * @param email User email
     * @param password User password
     * @param uiCallback OnTaskCompleteCallback to be executed on the UI thread.
     */
    public void signInUserWithEmailPassword(String email, String password,
                                            OnTaskCompleteCallback uiCallback) {
        sessionHandler.signInUserWithEmailPassword(email, password, uiCallback);
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
        sessionHandler.createUserWithEmailPassword(email, password, userType,  uiCallback);
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
        return sessionHandler.setLiveEventListener(onTaskCompleteCallback);
    }

    /**
     * Creates an Event object and sends it to the remote database with a generation request.
     * It then returns this event object
     *
     * @param eventType Type of event to generate
     * @return Event object
     */
    public Event generateLiveEvent(EventType eventType){
        return sessionHandler.generateLiveEvent(eventType);
    }

    /**
     * Removes the given event from the live_events in the database. This should be called when
     * the event has been dealt with.
     *
     * @param event Event object
     */
    public void disableLiveEvent(Event event){
        sessionHandler.disableLiveEvent(event);
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
        return sessionHandler.setLivePatientNotificationListener(callback);
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
        return sessionHandler.generateLivePatientNotification(patientUid, title, msg);
    }

    /**
     * Disables the live notification pointed by the input object (removes it from
     * live_patient_notifications)
     *
     * @param patientNotification PatientNotification object
     */
    public void disableLivePatientNotification(PatientNotification patientNotification){
        sessionHandler.disableLivePatientNotification(patientNotification);
    }

}
