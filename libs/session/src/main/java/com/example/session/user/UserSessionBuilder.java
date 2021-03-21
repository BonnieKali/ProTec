package com.example.session.user;

import android.util.Log;

import com.example.session.local.LocalDB;
import com.example.session.remote.RemoteDB;


public class UserSessionBuilder {
    private static final String TAG = "UserSessionBuilder";


    /**
     * Custom Exception occurs when the requested user was not found in the remote database
     */
    public static class UserNotFoundInRemoteException extends Exception{
        public UserNotFoundInRemoteException(String msg){
            super(msg);
        }
    }

    /**
     * Retrieves the previously saved UserSession from local database. If it does not exist, it
     * returns an empty, uninitialized userSession. (Make sure to check userSession.isInitialized)
     *
     * @param localDB LocalDB object
     * @return UserSession object
     */
    public static UserSession fromLocal(LocalDB localDB){
        UserSession userSession = localDB.getUserSession();

        if (userSession == null){
            Log.w(TAG, "fromLocal: Null returned from localdb. Returning empty UserSession");
            return new UserSession();
        } else {
            Log.d(TAG, "fromLocal: Returning saved UserSession");
            return userSession;
        }

    }

    /**
     * Constructs a UserSession object with the required id using data stored on the remote
     * database. If the user entry is not found, it throws a UserNotFoundInRemoteException.
     *
     * @param remoteDB RemoteDB object
     * @param uid Unique user identifier
     * @return UserSession object
     * @throws UserNotFoundInRemoteException
     */
    public static UserSession fromRemote(RemoteDB remoteDB, String uid)
            throws UserNotFoundInRemoteException {

        UserSession userSession = remoteDB.getUser(uid);

        if (userSession == null){
            Log.w(TAG, "fromRemote: Null returned from remote.");
            throw new UserNotFoundInRemoteException("User was not found on the database.");
        }
        return userSession;
    }

    /**
     * Constructs a new, empty UserSession for new users.
     *
     * @param uid Unique user identifier
     * @param email User email
     * @param userType UserInfo.UserType (PATIENT/CARER)
     * @return UserSession initialized object
     */
    public static UserSession buildNew(String uid, String email, UserInfo.UserType userType){
        UserInfo userInfo = new UserInfo(uid, email, userType);
        UserData userData = new UserData();
        return new UserSession(userInfo, userData);
    }

}
