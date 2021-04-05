package com.example.session.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.session.event.Event;
import com.example.session.patientNotifications.PatientNotification;
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
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * Evangelos Dimitriou (s1657192)
 * James Hanratty (s1645821)
 *
 * This class is responsible for reading and writing to firebase database.
 *
 * It uses JSON format to serialise UserData (PatientData and CarerData) objects and save/read them
 * to their corresponding database nodes. It accepts these objects, creates a HashMap of all
 * contained data classes and converts them to JSON. This simplifies addition of extra data to
 * patients and carers, since the database will accommodate them automatically without
 * additions/modifications
 *
 * It also registers listeners for changes in the remote database, and performs the registered
 * callbacks when changes happen.
 */


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
    private static final String PATIENT_SETTINGS = "patient_settings";
    private static final String LIVE_PATIENT_NOTIFICATIONS = "live_patient_notifications";
    private static final String PAST_PATIENT_NOTIFICATIONS = "past_patient_notifications";

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
     * Returns a HashMap<Patient ID, Patient Session> of all the patients in the remoteDB
     *
     * @return
     */
    public HashMap<String, PatientSession> getAllPatients(){
        HashMap<String, PatientSession> patientIDSessionMap = new HashMap<>();
        String result = getNode(dRef.child(PATIENTS));

        if (result.equals("")) {
            Log.w(TAG,"Result is nothing");
        }else{
            Gson gson = new Gson();
            Log.d(TAG,"Parsing result");
            try {
                patientIDSessionMap = gson.fromJson(result, new TypeToken<HashMap<String, PatientSession>>() {
                }.getType());
            }catch(Exception exception){
                Log.e(TAG, "Exception " + exception);
            }
                Log.d(TAG,"Finished parsing");
            Log.d(TAG,"patientIDSessionMap: " + patientIDSessionMap);
        }
        return patientIDSessionMap;
    }

    /**
     * Get all the users in the database
     * @return
     */
    public HashMap<String, UserInfo.UserType> getAllUsers(){
        HashMap<String, UserInfo.UserType> userId_Type = new HashMap<>();
        String result = getNode(dRef.child(USERS));

        if (!result.equals("")) {
            try{
                userId_Type = new Gson().fromJson(result,
                        new TypeToken<HashMap<String, UserInfo.UserType>>() {}.getType());
                Log.d(TAG, "userId_Type Map: " + userId_Type.values());
            }catch(Exception exception) {
                Log.e(TAG, "Exception " + exception);
            }
        }
        return userId_Type;
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
        Gson gson = new Gson();

        if (userType == UserInfo.UserType.CARER){
            result = getNode(dRef.child(CARERS).child(uid));
        }else{
            result = getNode(dRef.child(PATIENTS).child(uid));
        }

        if (result.equals("")){
            return null;
        } else {
            try{
                if (userType == UserInfo.UserType.CARER){
                    return gson.fromJson(result, CarerSession.class);
                }else{
                    return gson.fromJson(result, PatientSession.class);
                }
            }catch(Exception exception){
                Log.e(TAG, "Exception " + exception);
                return null;
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
        String result = getNode(dRef.child(USERS).child(uid));

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
                String jsonObject = String.valueOf(snapshot.getValue());

                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new StringReader(jsonObject));
                reader.setLenient(true);
                Event event = gson.fromJson(reader, Event.class);
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



    //-----------------------|
    // PATIENT NOTIFICATIONS |
    //-----------------------|

    public void generatePatientNotification(PatientNotification patientNotification){
        Map<String, Object> jsonMap = mapObject(patientNotification);

        // Get event key
        String key = patientNotification.getLiveKey();

        // Save to remote
        dRef.child(LIVE_PATIENT_NOTIFICATIONS).child(key).setValue(jsonMap);
        dRef.child(PAST_PATIENT_NOTIFICATIONS).push().setValue(jsonMap);
    }

    public void disablePatientNotification(PatientNotification patientNotification){
        // Retrieve the string corresponding to the event key from live_events and delete it
        String key = patientNotification.getLiveKey();
        dRef.child(LIVE_PATIENT_NOTIFICATIONS).child(key).removeValue();
    }

    public void setNewLivePatientNotificationListener(OnTaskCompleteCallback callback){
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: Added "+ snapshot.getValue());
                String jsonObject = String.valueOf(snapshot.getValue());

                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new StringReader(jsonObject));
                reader.setLenient(true);
                PatientNotification patientNotification = gson.fromJson(reader,
                        PatientNotification.class);
                callback.onComplete(new TaskResult<>(patientNotification));
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

        dRef.child(LIVE_PATIENT_NOTIFICATIONS).addChildEventListener(eventListener);
    }


    //------------------|
    // PATIENT SETTINGS |
    //------------------|

    /**
     * Retrieves all Settings entries for the specified patient.
     *
     * Returns a hashmap containing he settingKey -> settingValue. This is a blocking statement.
     *
     * @param uid Unique patient id
     * @return Map (setting_key -> setting_value)
     */
    public Map<String, Object> getPatientSettings(String uid){
        String result = getNode(dRef.child(PATIENT_SETTINGS).child(uid));
        Map<String, Object> ret = new HashMap<>();
        Gson gson = new Gson();

        Log.d(TAG,"getPatientSettings: returned: "+ result);

        if (!result.equals("")) {
            try {
                ret = gson.fromJson(result, new TypeToken<HashMap<String, Object>>() {
                }.getType());
            } catch (Exception exception) {
                Log.e(TAG, "Exception " + exception);
            }
        }
        return ret;
    }

    public void setPatientSetting(String uid, String settingKey, Object settingValue){
        Log.d(TAG, "SetPatientSetting is called with: " + uid + " " + settingKey + " " +
                settingValue.toString());
        dRef.child(PATIENT_SETTINGS).child(uid).child(settingKey).setValue(settingValue);
    }

    /**
     * Sets a listener to the firebase database for the given patients settings. The given callback
     * is called when there is any change to the settings of the patient.
     *
     * @param uid patient id
     * @param callback callback to be called with the value as input
     */
    public void setPatientSettingsListener(String uid, OnTaskCompleteCallback callback){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onComplete(new TaskResult<>(snapshot.getValue()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        dRef.child(PATIENT_SETTINGS).child(uid).addValueEventListener(valueEventListener);
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
     * Retrieves database reference as json string
     *
     * @param ref DatabaseReference
     * @return String data
     */
    private String getNode(DatabaseReference ref){
        String result = "";

        try {
            Task<DataSnapshot> task = ref.get();
            Tasks.await(task);

            if (task.isSuccessful()) {
                Log.d(TAG, "getNode: Success");

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
