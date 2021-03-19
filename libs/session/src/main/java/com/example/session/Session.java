package com.example.session;

import com.example.session.data.User;
import com.example.session.local.LocalDB;
import com.example.session.local.UserSession;
import com.example.session.remote.Authentication;
import com.example.session.remote.RemoteDB;

public class Session {
    private static final String TAG = "Session";

    // Static singleton com.example.session.Session instance
    private static Session instance;

    // Databases for data storage
    private LocalDB localDB;
    private RemoteDB remoteDB;

    // Authenticates users from remote Auth firebase portal
    private Authentication authentication;

    // Keeps all local user session information
    private UserSession userSession;


    /**
     * Singleton Session class.
     *
     * Responsible for initializing all persistent session components.
     *
     * Private constructor only called once, implementing singleton pattern.
     */
    private Session(){
        authentication = new Authentication();

        localDB = new LocalDB();
        remoteDB = new RemoteDB();

//        userSession = new UserSession();
        
    }

    /**
     * Returns the singleton Session instance. This is a thread-safe call
     *
     * @return Session singleton instance
     */
    public static synchronized Session getInstance(){
        if(instance == null) { instance = new Session(); }
        return instance;
    }


}
