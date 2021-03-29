package com.example.session;

import android.content.Context;
import android.util.Log;

import com.example.session.event.Event;
import com.example.session.event.EventBuilder;
import com.example.session.event.EventType;
import com.example.session.local.LocalDB;
import com.example.session.local.LocalLiveData;
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
        // TODO Update userSessions to localDB
//        userSessions = new HashSet<>();
        // TODO Is this needed in order for current user to get saved to remoteDB?
//        userSessions.add(userSession);
    }

    //-------------|
    // Data access |
    //-------------|

    public void addPatientModified(PatientSession patientState) {
        BackgroundPool.attachProcess(() -> localDB.addPatientModified(patientState));
//        userSessions.add(patientState);
//        BackgroundPool.attachProcess(() -> localDB.saveUserSession(patientState));
    }

    /**
     * Updates the localData with remote data
     */
    private void updateLocalData(){
        Log.d(TAG,"Updating local data...");
        // Get user id map from remote, and save to localDB
        HashMap<String, UserInfo.UserType> allUserTypes = remoteDB.getAllUsers();
        // Get all patient objects from remote, and save to localDB
        HashMap<String, PatientSession> allPatientIDSessions = remoteDB.getAllPatients();
        HashSet<PatientSession> allPatientSessions  = new HashSet<PatientSession>( allPatientIDSessions.values() );
        Log.d("debug","All patient sessions from remote data: " + allPatientSessions);
        // now update locallivedata
        localDB.updateAllPatientSessions(allUserTypes, allPatientSessions);
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
     * Updates the local data from remote with blocking
     */
    public void updateLocalDataFromRemote(){
        updateLocalData();
    }

     // -- retrieve data
    public HashSet<PatientSession> retrieveModifiedPatientSessions() {
        return localDB.retrieveModifiedPatientSessions();
    }

    public HashSet<PatientSession> retrieveAllPatientSessions() {
        return localDB.retrieveAllPatientSessions();
    }

    public HashSet<PatientSession> retrieveCarerPatientSessions(){
        return localDB.retrieveCarerPatientSessions();
    }

    public HashMap<String, UserInfo.UserType> retrieveUserIdTypeMap(){
        return localDB.retrieveUserIdTypeMap();
    }
    public boolean removePatientFromCarer(PatientSession patientSession) {
        return localDB.removePatientFromCarer(patientSession);
    }
    public boolean addPatientFromCarer(PatientSession patientSession) {
        return localDB.addPatientFromCarer(patientSession);
    }

    // ------------

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

        BackgroundPool.attachProcess(() -> {
            try {
                remoteDB.updateRemoteDBwithLocalDB(localDB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Saves all user data to the remote database in current thread (blocking statement).
     */
    public void syncToRemoteBlock(){
//        remoteDB.updateUsers(userSessions);
        try {
            remoteDB.updateRemoteDBwithLocalDB(localDB);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
