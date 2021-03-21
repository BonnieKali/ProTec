package com.example.session;

import android.content.Context;
import android.util.Log;

import com.example.session.local.LocalDB;
import com.example.session.remote.Authentication;
import com.example.session.remote.RemoteDB;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.UserSessionBuilder;
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
     * Saves all user data to the remote database in a background thread.
     */
    public void syncToRemote() {
        BackgroundPool.attachProcess(() -> remoteDB.updateUser(userSession.getUID(), userSession));
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
                                            OnTaskCompleteCallback uiCallback) {
        // TODO: Add UserType field in method input
        RunnableTask authenticationTask = () -> {
            TaskResult<String> userResult =
                    authentication.createUserWithEmailPassword(email, password);

            // If there was an error in the authentication return it directly to the UI
            if (userResult instanceof TaskResult.Error){
                return userResult;
            }

            // Build new UserSession and save it locally/remotely
            String userID = userResult.getData();

            // TODO: Add UserType field in method input
            this.userSession = UserSessionBuilder.buildNew(userID, email,
                    UserInfo.UserType.PATIENT);
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
}
