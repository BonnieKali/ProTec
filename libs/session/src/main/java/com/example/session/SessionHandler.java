package com.example.session;

import android.content.Context;
import android.util.Log;

import com.example.session.event.Event;
import com.example.session.event.EventBuilder;
import com.example.session.event.EventType;
import com.example.session.local.LocalDB;
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

public class SessionHandler {
    private static final String TAG = "SessionHandler";

    // Databases for data storage
    public LocalDB localDB;
    public RemoteDB remoteDB;

    // Authenticates users from remote Auth firebase portal
    public Authentication authentication;

    // Keeps all local user session information
    public UserSession userSession;


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
    // Patient Remote Access |
    //-----------------------|

    /**
     * Returns a PatientSession from the remote database. This is used by carer accounts to retrieve
     * patient data. This is a BLOCKING statement, so it should not be run in the UI thread.
     *
     * @param patientId Unique patient id
     * @return PatientSession
     */
    public PatientSession retrievePatientFromRemote(String patientId) throws
            RemoteDB.UserNotFoundException,
            RemoteDB.WrongUserTypeException {
        UserSession user = remoteDB.getUser(patientId);
        if (user == null){
            throw new RemoteDB.UserNotFoundException("User was not found in the database");
        }
        else if(user.userInfo.userType == UserInfo.UserType.CARER){
            throw new RemoteDB.WrongUserTypeException("User was a carer and not a patient");
        }
        return (PatientSession) user;
    }


}
