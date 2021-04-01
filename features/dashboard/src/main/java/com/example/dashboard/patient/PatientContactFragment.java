package com.example.dashboard.patient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dashboard.R;
import com.example.session.user.patient.PatientSession;

/**
 * create an instance of this fragment.
 */
public class PatientContactFragment extends Fragment {
    // Log
    String TAG_LOG = "PatientContactFragment";
    // Class Vars
    View view;
    // UI Vars
    TextView tv_email;
    // Patient Vars
    PatientSession patientSession;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_patient_contact, container, false);  // Inflate the layout for this fragment
        setPatientInfo();                                                                           // Args
        initializeUI();                                                                             // UI
        setOnClickListeners(view);                                                                  // set listeners
        return view;
    }


    /***
     * Setting up the Listeners
     * @param view
     */
    private void setOnClickListeners(View view) {
        setBtnStaticTestListener(view.findViewById(R.id.btn_stationary_test));
        setBtnDynamicTestListener(view.findViewById(R.id.btn_dynamic_test));
    }


    private void setBtnDynamicTestListener(Button button) {
        button.setOnClickListener(v -> {
            Log.d(TAG_LOG, "SEND LIVE REQUEST");
        });
    }


    private void setBtnStaticTestListener(Button button) {
        button.setOnClickListener(v -> {
            Log.d(TAG_LOG, "SEND LIVE REQUEST");
        });
    }


    /***
     * Function Deals setting patient information variables
     */
    public void setPatientInfo(){
        patientSession = getArguments().getParcelable("patientSession");
    }


    /***
     * Function Initializes UI elements
     */
    private void initializeUI() {
        tv_email = view.findViewById(R.id.tv_email_value);
        tv_email.setText(patientSession.userInfo.email);
    }


}