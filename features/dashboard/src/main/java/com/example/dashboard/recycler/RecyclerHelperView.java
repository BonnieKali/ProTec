package com.example.dashboard.recycler;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;

import java.util.ArrayList;
import java.util.HashSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerHelperView {

    private static final String TAG = "RecyclerHelperView";

    private RecyclerView mRecyclerView; // shows patients
    private PatientAdapter mAdapter;  //deals with loading items to recyclerviewer
    private RecyclerView.LayoutManager mLayoutManager;  // sets up the list viewer

    ArrayList<PatientItem> patientItems;
    View view;
    CarerSession user;
    Session session;

    public RecyclerHelperView(View view, CarerSession user) {
        this.view = view;
        this.user = user;
        this.session = Session.getInstance();
    }

    // -- Recycler Viewer -- //
    /**
     * Initialise the Patient Recycler Viewer
     * @param view
     */
    public void initialisePatientView(View view, Context context) {
        HashSet<PatientSession> patientSessions = session.retrieveAllPatientSessions();
        Log.d("debug","All patient sessions loaded: " + patientSessions);
        patientItems = PatientItem.initialisePatients(view, patientSessions);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context);
        mAdapter = new PatientAdapter(patientItems, context);
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
        user.carerData.removePatient(patientSession);

        HashSet<PatientSession> patientSessions = session.retrieveAllPatientSessions();
        Log.d("debug","All patient sessions loaded after removing patient: " + patientSessions);

        Log.d(TAG, "Patient after being removed: " + patientSession.patientData);
        Log.d(TAG, "carer after removing Patient: " + user.carerData);

//        Toast.makeText(getContext(),"Patient Removed",Toast.LENGTH_SHORT).show();
        mAdapter.notifyItemChanged(position);   // this calls PatientAdapter.onBindViewHolder
        session.saveState();    // saves carers sate
//        session.savePatientState(patientSession); // saves patientState
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

        // TODO make a session method that updates both of these instead of manually doing it
        patientSession.patientData.relationship.addCarer(user.getUID());
        user.carerData.addPatient(patientSession);

        HashSet<PatientSession> patientSessions = session.retrieveAllPatientSessions();
        Log.d("debug","All patient sessions loaded after adding patient: " + patientSessions);

        Log.d(TAG, "Patient after being added: " + patientSession.patientData);
        Log.d(TAG, "carer after adding Patient: " + user.carerData);

//        Toast.makeText(getContext(), "Patient Added", Toast.LENGTH_SHORT).show();
        mAdapter.notifyItemChanged(position); // this calls PatientAdapter.onBindViewHolder
        session.saveState();
//        session.savePatientState(patientSession);
    }

    /**
     * This method deals with changing the patientItem data and updates
     * the recycler viewer displaying it
     * @param position
     */
    private void changeItem(int position) {
        // this seems to induce a wierd bug where the
//        mAdapter.notifyItemChanged(position);
//        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
    }
    // ----------------------------

}
