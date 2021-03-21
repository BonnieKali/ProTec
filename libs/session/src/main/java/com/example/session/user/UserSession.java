package com.example.session.user;


public class UserSession {

    // Specifies whether the UserSession is initialized (if user is logged out, this is
    // not initialized)
    private Boolean initialized;

    // Object containing all user information
    public UserInfo userInfo;


    /**
     * Default empty constructor
     */
    public UserSession(){
        this.userInfo = null;
        this.initialized = false;
    }

    /**
     * Constructor with initialized userInfo and userData objects
     */
    public UserSession(UserInfo userInfo){
        this.userInfo = userInfo;
        this.initialized = true;
    }

    /**
     * Public call returning the initialization state of this object
     *
     * @return isInitialized
     */
    public Boolean isInitialised(){ return this.initialized; }

    /**
     * Returns unique user id of the current user
     *
     * @return String unique user identifier
     */
    public String getUID(){
        if(this.isInitialised()){
            return userInfo.id;
        }else{
            return null;
        }
    }

    /**
     * Returns the user type of this account (PATIENT/CARER)
     *
     * @return UserType of account
     */
    public UserInfo.UserType getType(){
        if(this.isInitialised()){
            return userInfo.userType;
        }else{
            return null;
        }
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "initialized=" + initialized +
                ", userInfo=" + userInfo +
                '}';
    }
}
