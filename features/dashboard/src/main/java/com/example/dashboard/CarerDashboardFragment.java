package com.example.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.actions.Actions;
import com.example.dashboard.recycler.PatientAdapter;
import com.example.dashboard.recycler.PatientItem;
import com.example.session.Session;
import com.example.session.remote.RemoteDB;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.example.dashboard.recycler.PatientItem.initialisePatients;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarerDashboardFragment extends Fragment {
    private static final String TAG = "CarerDashboardFragment";

    private RecyclerView mRecyclerView; // shows patients
    private PatientAdapter mAdapter;  //deals with loading items to recyclerviewer
    private RecyclerView.LayoutManager mLayoutManager;  // sets up the list viewer

    ArrayList<PatientItem> patientItems;

    TextView carerTextView;

    private Session session;
    private CarerSession user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carer_dashboard, container, false);

        session = Session.getInstance();
        user = (CarerSession) session.getUser();

        // Say hello to user (for testing
        carerTextView = view.findViewById(R.id.carer_textView);
        carerTextView.setText("Hello "+session.getUser().userInfo.email);

        // Start service for notifications
        startNotificationService();
        // Set Listeners for clickable items
        setOnClickListeners(view);

//        initialisePatientView(view);
//        loadPatients(view);
        loadAllPatients(view);
        return view;
    }

    /**
     * Loads all the data from every patient in the remote database
     * @param v
     */
    private void loadAllPatients(View v){
        UserSession user = session.getUser();
        HashMap<String, UserInfo.UserType> allUsers;
//        String patient_id_of_carer = "NdSBSeOx47TC9cGRKFe35tsXBU83";
//        HashSet<String> patient_ids = ((CarerSession) user).carerData.patients;
        // background process to get patients from carers
        RunnableTask get_patients = () ->
                new TaskResult<List>(getAllPatientsFromDB());

        // method called when get_patients has completeds
        OnTaskCompleteCallback callback = taskResult -> {
            initialisePatientView(v, (List<PatientSession>) taskResult.getData());
        };

        // run the task
        BackgroundPool.attachTask(get_patients, callback);
    }

    /**
     * Gets the PatientSession for every patient in the database
     * @return
     */
    private List<PatientSession> getAllPatientsFromDB() {
        Log.d(TAG,"Getting All users ");
        List<PatientSession> users = new ArrayList<>();
        try {
            Map<String, UserInfo.UserType> userID_Types = session.retrieveUserIDsFromRemote();
            for (String id: userID_Types.keySet()){
                // only get patients
                Log.d(TAG,"Checking patient: " + id);
                if (userID_Types.get(id) == UserInfo.UserType.PATIENT) {
                    PatientSession patient = session.retrievePatientFromRemote(id);
                    users.add(patient);
                }
            }
        } catch (RemoteDB.WrongUserTypeException e) {
            Log.e(TAG, "Error retireveing all users");
            Log.e(TAG, "Error:" + e);
            e.printStackTrace();
        } catch (RemoteDB.UserNotFoundException e) {
            Log.e(TAG, "Error retireveing all users");
            Log.e(TAG, "Error:" + e);
            e.printStackTrace();
        }
        Log.d(TAG,"Users are: " + users);
        return users;
    }

    // -- Recycler Viewer -- //
    /**
     * Initialise the Patient Recycler Viewer
     * @param view
     */
    private void initialisePatientView(View view, List<PatientSession> patientSessions ) {
        patientItems = PatientItem.initialisePatients(view, patientSessions);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new PatientAdapter(patientItems, getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // se this on item click listener for when someone touches the patient list
        mAdapter.setOnItemClickListener(new PatientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeItem(position);
            }

            @Override
            public void onPatientAddClick(int position, View v) {
                addPatient(position, v);
            }

            @Override
            public void onPatientRemoveClick(int position, View v) {
                removePatient(position, v);
            }
        });
    }

    /**
     * Removes the patient from the carers patients list
     * @param position
     * @param v
     */
    private void removePatient(int position, View v) {
        PatientItem patient =  patientItems.get(position);
        PatientSession patientSession = patient.getSession();

        patientSession.patientData.relationship.removeCarer(user.getUID());
        user.carerData.removePatient(patient.getID());

        Log.d(TAG, "Patient after being removed: " + patientSession.patientData);
        Log.d(TAG, "carer after removing Patient: " + user.carerData);

        Toast.makeText(getContext(),"Patient Removed",Toast.LENGTH_SHORT).show();
        mAdapter.notifyItemChanged(position);   // this calls PatientAdapter.onBindViewHolder
        session.saveState();
        session.savePatientState(patientSession);
    }

    /**
     * Adds the patient to the carers list of patients
     * @param position
     * @param v
     */
    private void addPatient(int position, View v) {
        // set the patient to belong to the carer
        PatientItem patient =  patientItems.get(position);
        PatientSession patientSession = patient.getSession();

        patientSession.patientData.relationship.addCarer(user.getUID());
        user.carerData.addPatient(patient.getID());

        Log.d(TAG, "Patient after being added: " + patientSession.patientData);
        Log.d(TAG, "carer after adding Patient: " + user.carerData);

        Toast.makeText(getContext(), "Patient Added", Toast.LENGTH_SHORT).show();
        mAdapter.notifyItemChanged(position); // this calls PatientAdapter.onBindViewHolder
        session.saveState();
        session.savePatientState(patientSession);
    }

    /**
     * This method deals with changing the patientItem data and updates
     * the recycler viewer displaying it
     * @param position
     */
    private void changeItem(int position) {
        // this seems to induce a wierd bug where the
//        patientItems.get(position).setmText2("Ilie is super gay");
//        mAdapter.notifyItemChanged(position);
    }
    // ----------------------------

    // -- Button Listeners -- //

    /**
     * custom on click listener
     * @param view view of the inflated view (all elements on screen)
     */
    private void setOnClickListeners(View view) {
        setOpenMapButtonListener(view.findViewById(R.id.open_map_btn)); // Map Button
    }

    /**
     * Sets up what happens when the button is clicked and opens the
     * map
     * @param button_open_map
     */
    private void setOpenMapButtonListener(Button button_open_map) {
        button_open_map.setOnClickListener(v -> {
            Activity act = getActivity();
            if(act != null){
                Intent intent = Actions.openMapIntent(act);
                act.startActivity(intent);
            }
        });
    }
    // -- -------------- -- //


    /**
     * Starts the notification service in the background
     */
    private void startNotificationService() {
        Activity act = getActivity();
        if (act != null){
            Log.d(TAG, "Activity was not null!. Starting notification service");
            act.startService(Actions.getNotificationServiceIntent(act));
        }else{
            Log.w(TAG, "Activity was null!. Cannot start service!");
        }
    }
}