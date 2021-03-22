package com.example.session.remote;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.session.event.Event;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private static final String PATIENTS = "patients";
    private static final String CARERS = "carers";
    private static final String LIVE_EVENTS = "live_events";
    private static final String PAST_EVENTS = "past_events";

    // Database reference
    private FirebaseDatabase database;
    private DatabaseReference dRef;


    /**
     * Class responsible for all remote database read/write requests
     */
    public RemoteDB(){
        // Variables
        database = FirebaseDatabase.getInstance(DB_URL);
        dRef = database.getReference();
    }


    //--------------|
    // USER ACTIONS |
    //--------------|

    /**
     * Updates the information and data entries of the selected user with the data contained in
     * the UserSession object. If the userSession is of type CARER/PATIENT it will be saved to
     * the corresponding table.
     *
     * @param uid   Unique user identifier
     * @param userSession   UserSession object containing all user info/data
     */
    public void updateUser(String uid, UserSession userSession){
        // Turn userSession object into a JSON Map
        Map<String, Object> jsonMap = mapObject(userSession);

        // Overwrite the previous UserType entry assigned to this UID
        dRef.child(USERS).child(uid).setValue(userSession.getType().toString());

        // Send the JSON data to the corresponding database table
        if (userSession.getType() == UserInfo.UserType.CARER){
            Log.d(TAG, "updateUser: Updating Carer account");
            dRef.child(CARERS).child(uid).updateChildren(jsonMap);
        } else {
            Log.d(TAG, "updateUser: Updated Patient account");
            dRef.child(PATIENTS).child(uid).updateChildren(jsonMap);
        }
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
        UserInfo.UserType userType = getUserType(uid);
        String result = "";

        try {
            Task<DataSnapshot> task;
            if (userType == UserInfo.UserType.CARER){
                task = dRef.child(CARERS).child(uid).get();
            }else{
                task = dRef.child(PATIENTS).child(uid).get();
            }
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

            if (userType == UserInfo.UserType.CARER){
                return gson.fromJson(result, CarerSession.class);
            }else{
                return gson.fromJson(result, PatientSession.class);
            }
        }
    }


    /**
     * Returns the UserType associated with the given UID. This is a blocking statement.
     *
     * @param uid Unique user identifier
     * @return object of UserType enum
     */
    private UserInfo.UserType getUserType(String uid){
        String result = "";

        try {
            Task<DataSnapshot> task = dRef.child(USERS).child(uid).get();
            Tasks.await(task);

            if (task.isSuccessful()) {
                Log.d(TAG, "getUserType: Success");

                if(task.getResult() != null){
                    result = String.valueOf(task.getResult().getValue());
                    Log.d(TAG, "getUserType: Result is "+result);
                }else{
                    Log.w(TAG, "getUserType: Result is null");
                }
            }
            else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        } catch (ExecutionException e) {
            Log.w(TAG, "getUserType: Failure", e);
        } catch (InterruptedException e) {
            Log.w(TAG, "getUserType: Failure", e);
        }

        if (result.equals("")){
            return null;
        } else {
            if (result.equals(UserInfo.UserType.CARER.toString())){
                return UserInfo.UserType.CARER;
            }else {
                return UserInfo.UserType.PATIENT;
            }
        }
    }


    //---------------|
    // EVENT ACTIONS |
    //---------------|

    /**
     * Adds Event to live_events and past_events table in remote database.
     *
     * @param event Event to generate
     */
    public void generateEvent(Event event){
        Map<String, Object> jsonMap = mapObject(event);

        // Get event key
        String key = event.getLiveKey();

        // Save to remote
        dRef.child(LIVE_EVENTS).child(key).setValue(jsonMap);
        dRef.child(PAST_EVENTS).push().setValue(jsonMap);
    }

    /**
     * Deletes the live event if it exists.
     *
     * @param event live Event to disable
     */
    public void disableEvent(Event event){
        // Retrieve the string corresponding to the event key from live_events and delete it
        String key = event.getLiveKey();
        dRef.child(LIVE_EVENTS).child(key).removeValue();
    }


    /**
     * Sets a listener for new live events on the remote database.
     *
     * @param onTaskCompleteCallback callback called when there is a new live event. The TaskResult
     *                               argument of the callback will contain the Event object inside
     *                               its data.
     */
    public void setNewLiveEventListener(OnTaskCompleteCallback onTaskCompleteCallback){
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: Added "+ snapshot.getValue());
                Event event = snapshot.getValue(Event.class);
                onTaskCompleteCallback.onComplete(new TaskResult<>(event));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadEvent:onCancelled", databaseError.toException());
            }
        };

        dRef.child(LIVE_EVENTS).addChildEventListener(eventListener);
    }


    //-----------|
    // UTILITIES |
    //-----------|

    /**
     * Maps Java object to a HashMap using JSON google library
     *
     * @param object Java object
     * @return Map (String -> Object)
     */
    private Map<String, Object> mapObject(Object object){
        Gson json = new Gson();
        String userSessionString = json.toJson(object, object.getClass());
        return json.fromJson(userSessionString,
                new TypeToken<HashMap<String, Object>>() {}.getType());
    }


    /**
     *  Retrieves data at requested node reference
     *
     * @param ref DatabaseReference object
     * @return
     */
    private String getNode(DatabaseReference ref){
        String result = "";

        try {
            Task<DataSnapshot> task = ref.get();
            Tasks.await(task);

            if (task.isSuccessful()) {
                Log.d(TAG, "getNode Success: "+ref.toString());

                if(task.getResult() != null){
                    result = String.valueOf(task.getResult().getValue());
                    Log.d(TAG, "getNode: Result is "+result);
                }else{
                    Log.w(TAG, "getNode: Result is null");
                }
            }
            else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        } catch (ExecutionException e) {
            Log.w(TAG, "getNode: Failure", e);
        } catch (InterruptedException e) {
            Log.w(TAG, "getNode: Failure", e);
        }

        return result;
    }

}
