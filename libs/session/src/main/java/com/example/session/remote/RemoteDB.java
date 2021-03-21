package com.example.session.remote;

import android.util.Log;

import com.example.session.user.UserSession;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class RemoteDB {
    private static final String TAG = "RemoteDB";

    // Database related constants
    private static final String DB_URL =
            "https://protec-546cd-default-rtdb.europe-west1.firebasedatabase.app/";

    private static final String USERS = "users";
    private static final String EVENTS = "events";

    // Database reference
    private static DatabaseReference dRef;


    /**
     * Class responsible for all remote database read/write requests
     */
    public RemoteDB(){
        // Variables
        FirebaseDatabase database = FirebaseDatabase.getInstance(DB_URL);
        dRef = database.getReference();
    }


    /**
     * Updates the information and data entries of the selected user with the data contained in
     * the UserSession object
     *
     * @param uid   Unique user identifier
     * @param userSession   UserSession object containing all user info/data
     */
    public void updateUser(String uid, UserSession userSession){
        Gson json = new Gson();
        String userSessionString = json.toJson(userSession, UserSession.class);

        Map<String, Object> jsonMap = json.fromJson(userSessionString,
                new TypeToken<HashMap<String, Object>>() {}.getType());
        dRef.child(USERS).child(uid).updateChildren(jsonMap);
    }


    /**
     * Retrieves the entry for the requested user from the remote database, and returns an
     * initialized object of type UserSession. If the query fails for any reason, it returns null.
     * This is a blocking statement and as a result it should be run on a non-UI thread.
     *
     * @param uid Unique user identifier
     * @return UserSession object
     */
    public UserSession getUser(String uid){
        String result = "";

        try {
            Task<DataSnapshot> task = dRef.child(USERS).child(uid).get();
            Tasks.await(task);

            if (task.isSuccessful()) {
                Log.d(TAG, "getUser: Success");

                if(task.getResult() != null){
                    result = String.valueOf(task.getResult().getValue());
                    Log.d(TAG, "getUser: Result is "+result);
                }else{
                    Log.w(TAG, "getUser: Result is null");
                }
            }
            else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        } catch (ExecutionException e) {
            Log.w(TAG, "getUser: Failure", e);
        } catch (InterruptedException e) {
            Log.w(TAG, "getUser: Failure", e);
        }

        if (result.equals("")){
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(result, UserSession.class);
        }
    }

}
