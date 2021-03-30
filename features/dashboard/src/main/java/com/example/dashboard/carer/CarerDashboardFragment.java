package com.example.dashboard.carer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.actions.Actions;
import com.example.dashboard.R;
import com.example.dashboard.recycler.RecyclerHelperView;
import com.example.session.Session;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;
import com.example.threads.OnTaskCompleteCallback;

import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarerDashboardFragment extends Fragment {
    private static final String TAG = "CarerDashboardFragment";

//    private RecyclerView mRecyclerView; // shows patients
//    private PatientAdapter mAdapter;  //deals with loading items to recyclerviewer
//    private RecyclerView.LayoutManager mLayoutManager;  // sets up the list viewer
//
//    ArrayList<PatientItem> patientItems;

    TextView carerTextView;

    RecyclerHelperView recyclerHelperView;

    private Session session;
    private CarerSession user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carer_dashboard, container, false);

        session = Session.getInstance();
        user = (CarerSession) session.getUser();
        recyclerHelperView = new RecyclerHelperView(view, user);

        // Say hello to user (for testing
        carerTextView = view.findViewById(R.id.carer_textView);
        carerTextView.setText("Hello "+session.getUser().userInfo.email);

        // Start service for notifications
        startBackgroundService();
        // Set Listeners for clickable items
        setOnClickListeners(view);
        loadAllPatients(view);
        return view;
    }

    /**
     * Loads all the data from every patient in the remote database
     * @param v
     */
    private void loadAllPatients(View v){
        Log.d(TAG,"Loading All Data");
        OnTaskCompleteCallback callback = taskResult -> {
            recyclerHelperView.initialisePatientView(v, this.getContext());
        };
        // run the task
        session.updateLocalDataFromRemote(callback);
    }

    // -- Button Listeners -- //
    /**
     * custom on click listener
     * @param view view of the inflated view (all elements on screen)
     */
    private void setOnClickListeners(View view) {
        setOpenMapButtonListener(view.findViewById(R.id.open_map_btn)); // Map Button
    }

    /**
     * Sets up what happens when the button is clicked and opens the map
     * @param button_open_map
     */
    private void setOpenMapButtonListener(Button button_open_map) {
        button_open_map.setOnClickListener(v -> {
            HashSet<PatientSession> patientSessions = session.retrieveAllPatientSessions();
            Log.d("debug","All patient sessions before opening Map: " + patientSessions);
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
    private void startBackgroundService() {
        Activity act = getActivity();
        if (act != null){
            Log.d(TAG, "Activity was not null!. Starting BackgroundService");
            act.startService(Actions.geBackgroundServiceIntent(act));
        }else{
            Log.w(TAG, "Activity was null!. Cannot start BackgroundService!");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"Resuming Activity...");
//        HashSet<PatientSession> patientSessions = session.retrieveAllPatientSessions();
//        Log.d("debug","All patient sessions when opening carerDashboard: " + patientSessions);
//        loadAllPatients(this.getView());
    }

    @Override
    public void onPause(){
        // synch to remote
        if (session.getUser().isInitialised()) {
            Session.getInstance().syncToRemote();   //
        }
        super.onPause();
    }
}