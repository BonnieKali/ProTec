package com.example.dashboard.carer;

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
import com.example.dashboard.R;
import com.example.dashboard.recycler.PatientAdapter;
import com.example.dashboard.recycler.PatientItem;
import com.example.dashboard.recycler.RecyclerHelperView;
import com.example.session.Session;
import com.example.session.remote.RemoteDB;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableProcess;
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
        startNotificationService();
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
     * Sets up what happens when the button is clicked and opens the
     * map
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
    private void startNotificationService() {
        Activity act = getActivity();
        if (act != null){
            Log.d(TAG, "Activity was not null!. Starting notification service");
            act.startService(Actions.getNotificationServiceIntent(act));
        }else{
            Log.w(TAG, "Activity was null!. Cannot start service!");
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