package com.example.dashboard.patient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dashboard.R;
import com.example.session.user.data.deadreckoning.DRData;
import com.example.session.user.patient.PatientSession;

import java.util.ArrayList;
import java.util.List;

/**
 * create an instance of this fragment.
 */
public class PatientTrackingFragment extends Fragment {
    View view;
    PatientSession patientSession;
    //ArrayList<DRData> patientIndoorData;
    List<DRData> patientIndoorData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_patient_tracking, container, false);
        setPatientInfo();
        setOnClickListeners(view);                                                                  // set listeners
        return view;
    }

    /***
     * Button Listeners
     * @param view
     */
    private void setOnClickListeners(View view) {
        setBtnDay1Listener(view.findViewById(R.id.btn_day1));
        setBtnDay2Listener(view.findViewById(R.id.btn_day2));
    }


    private void setBtnDay1Listener(Button button) {
        button.setOnClickListener(v -> {
            // Switch to day 1 fragment
            PatientTrackingDayFragment patientTrackingDayFragment = new PatientTrackingDayFragment();
            switchToFragment(patientTrackingDayFragment,"1");
        });
    }

    private void setBtnDay2Listener(Button button) {
        button.setOnClickListener(v -> {
            // Switch to day 2 fragment
            PatientTrackingDayFragment patientTrackingDayFragment = new PatientTrackingDayFragment();
            switchToFragment(patientTrackingDayFragment,"31");
        });
    }


    /***
     * Function Deals setting patient information variables
     */
    public void setPatientInfo(){
        patientSession = getArguments().getParcelable("patientSession");
        // get other stuff from patietn session here
        patientIndoorData = patientSession.patientData.drData;



    }

    /***
     * Changes between fragments
     * @param fragment
     */
    private void switchToFragment(Fragment fragment, String day) {
        // Set Args to new fragment
        Bundle args = new Bundle();
        args.putParcelable("patientSession", patientSession);
        args.putString("day_id", day);
        fragment.setArguments(args);
        // Transition to new fragment
        FragmentManager fragmentManager = ((FragmentActivity)getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}