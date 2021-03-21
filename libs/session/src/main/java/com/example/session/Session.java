package com.example.session;

import android.content.Context;

import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.threads.OnTaskCompleteCallback;


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
     * Called on application creation to set a static app context
     *
     * @param context Application context
     */
    public static void initialize(Context context){
        appContext = context;
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


    //--------------------|
    // UserSession Access-|
    //--------------------|

    /**
     * Returns the active UserSession object
     *
     * @return UserSession object
     */
    public UserSession getUser(){
        return sessionHandler.userSession;
    }


}
