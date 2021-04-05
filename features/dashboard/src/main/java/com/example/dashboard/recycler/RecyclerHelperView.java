package com.example.dashboard.recycler;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

        import com.example.dashboard.R;
import com.example.dashboard.patient.PatientInfoFragment;
import com.example.session.Session;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;

        import java.util.ArrayList;
import java.util.HashSet;

//import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * James Hanratty (s1645821)
 * This class contains the methods to set up the RecycleView which displays the
 * patients to the carer in a listview
 */
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
                addPatient(position, view);
                changeItem(position, context);
            }

            @Override
            public void onPatientAddClick(int position, View v)
            {
                Log.d(TAG,"patient add click");
                addPatient(position, v);
            }

            @Override
            public void onPatientRemoveClick(int position, View v) {
                Log.d(TAG,"Remive click");                          // james ... you dyslexic  XD
                removePatient(position, v);
            }
        });
    }

    /**
     * Removes the patient from the carers patients list
     * @param position: The position of the patient in the patientItems list
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
     * @param position: The position of the patient in the patientItems list
     * @param v
     */
    private void addPatient(int position, View v) {
        // set the patient to belong to the carer
        PatientItem patient =  patientItems.get(position);
        PatientSession patientSession = patient.getSession();

        user.carerData.addPatient(patientSession);

        mAdapter.notifyItemChanged(position); // this calls PatientAdapter.onBindViewHolder
        session.saveState();
    }


    /**
     * This method deals with changing the patientItem data and updates
     * the recycler viewer displaying it
     * @param position: The position of the patient in the patientItems list
     */
    private void changeItem(int position, Context context) {
        // this seems to induce a wierd bug where the
        PatientItem patient = patientItems.get(position);
        switchPatientInfoFragment(patient,context);
    }


    /***
     * Function that transitions to Patient Info Fragment
     * @param patientItem: The patientItem
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
