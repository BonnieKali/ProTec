package com.example.dashboard.recycler;

import android.app.Activity;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import androidx.fragment.app.Fragment;
//import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerHelperView{

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
                Log.d(TAG,"item click");
                changeItem(position, context, view);
            }

            @Override
            public void onPatientAddClick(int position, View v)
            {
                Log.d(TAG,"patient add click");
                addPatient(position, v);
            }

            @Override
            public void onPatientRemoveClick(int position, View v) {
                Log.d(TAG,"Remive click");
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

        user.carerData.removePatient(patientSession);

        mAdapter.notifyItemChanged(position);   // this calls PatientAdapter.onBindViewHolder
        session.saveState();    // saves carers sate
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

        user.carerData.addPatient(patientSession);
<<<<<<< HEAD
=======

        HashSet<PatientSession> patientSessions = session.retrieveAllPatientSessions();
        Log.d("debug","All patient sessions loaded after adding patient: " + patientSessions);

        Log.d(TAG, "Patient after being added: " + patientSession.patientData);
        Log.d(TAG, "carer after adding Patient: " + user.carerData);

>>>>>>> dev
        mAdapter.notifyItemChanged(position); // this calls PatientAdapter.onBindViewHolder
        session.saveState();
    }


    /**
     * This method deals with changing the patientItem data and updates
     * the recycler viewer displaying it
     * @param position
     */
    private void changeItem(int position, Context context, View v) {
        // this seems to induce a wierd bug where the
        PatientItem patient = patientItems.get(position);

        // --------- Patient Session Example --------- //
        PatientSession patientSession= patient.getSession();
        Log.d(TAG,"changeItem" + "\n" + "userInfo: "+ patientSession.userInfo);
        session.getPatientSettings(patientSession.userInfo.id, new OnTaskCompleteCallback() {
            @Override
            public void onComplete(TaskResult<?> taskResult) {
                Map<String, Object> settings = (Map<String, Object>) taskResult.getData();
                Log.d(TAG,settings.toString());
            }
        });
        session.setPatientSetting(patientSession.userInfo.id,"James",true);
        // --------------------------- //
        switchPatientInfoFragment(patient,context);
    }


    /***
     * Function that transitions to Patient Info Fragment
     * @param patientItem
     * @param context
     */
    private void switchPatientInfoFragment(PatientItem patientItem, Context context){
        // Get Patient id
        String patientID= patientItem.getSession().userInfo.id;
        // Set Args to new fragment
        PatientInfoFragment patientInfoFragment = new PatientInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("patientSession", patientItem.getSession());
        patientInfoFragment.setArguments(args);
        // Transition to new fragment
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, patientInfoFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

}
