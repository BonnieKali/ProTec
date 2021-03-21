package com.example.session.user;

import android.util.Log;

import com.example.session.local.LocalDB;
import com.example.session.remote.RemoteDB;
import com.example.session.user.carer.CarerData;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientData;
import com.example.session.user.patient.PatientSession;


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
            if (userSession.getType() == UserInfo.UserType.CARER){
                Log.d(TAG, "fromLocal: Returning saved CarerSession");
                return (CarerSession) userSession;
//                return new CarerSession(userSession.userInfo, (CarerData) userSession.userData);
            } else {
                Log.d(TAG, "fromLocal: Returning saved PatientSession");
                return (PatientSession) userSession;
//                return new PatientSession(userSession.userInfo, (PatientData) userSession.userData);
            }
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

        if (userSession.getType() == UserInfo.UserType.CARER){
            Log.d(TAG, "fromRemote: Returning saved CarerSession");
            return (CarerSession) userSession;
//            return new CarerSession(userSession.userInfo, (CarerData) userSession.userData);
        } else {
            Log.d(TAG, "fromRemote: Returning saved PatientSession");
            return (PatientSession) userSession;
//            return new PatientSession(userSession.userInfo, (PatientData) userSession.userData);
        }
    }

    /**
     * Constructs a new, empty UserSession for new users. Depending on the UserType it will return
     * a CarerSession or PatientSession object
     *
     * @param uid Unique user identifier
     * @param email User email
     * @param userType UserInfo.UserType (PATIENT/CARER)
     * @return UserSession initialized object
     */
    public static UserSession buildNew(String uid, String email, UserInfo.UserType userType){
        UserInfo userInfo = new UserInfo(uid, email, userType);

        if (userType == UserInfo.UserType.CARER) {
            CarerData carerData = new CarerData();
            return new CarerSession(userInfo, carerData);
        }
        else {
            PatientData patientData = new PatientData();
            return new PatientSession(userInfo, patientData);
        }

    }

}
