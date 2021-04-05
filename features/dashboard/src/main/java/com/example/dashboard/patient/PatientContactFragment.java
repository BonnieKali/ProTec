package com.example.dashboard.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.actions.Actions;
import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.patient.PatientSession;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;

import java.util.ArrayList;
import java.util.Map;

/**
 * create an instance of this fragment.
 */
public class PatientContactFragment extends Fragment {
    // Log
    private String TAG_LOG = "PatientContactFragment";
    // Class Vars
    private View view;
    // UI Vars
    private TextView tv_email, tv_static, tv_dynamic, tv_fallen_state;
    // Patient Vars
    private PatientSession patientSession;
    private int counts_static, counts_dynamic;
    private boolean fallen_state_value;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_patient_contact, container, false);  // Inflate the layout for this fragment
        setPatientInfo();                                                                           // Args
        initializeUI();                                                                             // UI
        setOnClickListeners(view);                                                                  // set listeners
        setUI();
        return view;
    }


    /***
     * Function responsible for updating the Ui
     */
    private void setUI() {
        tv_static.setText(""+counts_dynamic);
        tv_dynamic.setText(""+counts_dynamic);
        tv_fallen_state.setText(""+fallen_state_value);
    }


    /***
     * Setting up the Listeners
     * @param view
     */
    private void setOnClickListeners(View view) {
        setBtnStaticTestListener(view.findViewById(R.id.btn_stationary_test));
        setBtnDynamicTestListener(view.findViewById(R.id.btn_dynamic_test));
        setBtnShowLastLocationListener(view.findViewById(R.id.btn_last_known_location));
        setBtnTrackingListener(view.findViewById(R.id.btn_tracking));
    }


    /***
     * NACHO SETS LISTENER FUNCTION
     * @param button
     */
    private void setBtnTrackingListener(Button button) {
        button.setOnClickListener(v -> {
            PatientTrackingFragment patientTrackingFragment = new PatientTrackingFragment();
            switchToFragment(patientTrackingFragment);
        });
    }


    /***
     * Calls the map and shows the last location of the user
     * @param button
     */
    private void setBtnShowLastLocationListener(Button button) {
        button.setOnClickListener(v -> {
            Activity act = getActivity();
            if (act != null) {
                Intent intent = Actions.openMapIntent(act);
                act.startActivity(intent);
            }
        });
    }


    private void setBtnDynamicTestListener(Button button) {
        button.setOnClickListener(v -> {
            Log.d(TAG_LOG, "SEND LIVE REQUEST");
            Session.getInstance().generateLivePatientNotification(patientSession.getUID(),
                    "REQUEST","DYNAMIC");
        });
    }


    private void setBtnStaticTestListener(Button button) {
        button.setOnClickListener(v -> {
            Log.d(TAG_LOG, "SEND LIVE REQUEST");
            Session.getInstance().generateLivePatientNotification(patientSession.getUID(),
                    "REQUEST","STATIC");
        });
    }


    /***
     * Function Deals setting patient information variables
     */
    public void setPatientInfo(){
        patientSession = getArguments().getParcelable("patientSession");
        ((TextView)view.findViewById(R.id.patient_textView)).setText(patientSession.userInfo.getUserName()); // set user name on ui
        getPatientData();
    }


    /***
     * Retrieves the patient data from the database
     */
    private void getPatientData() {
        setTestCounts();
        setFallenState();
    }


    /***
     * Gets settings value end displays it
     */
    private void setFallenState() {
        Session.getInstance().getPatientSettings(patientSession.userInfo.id, new OnTaskCompleteCallback() {
            @Override
            public void onComplete(TaskResult<?> taskResult) {
                Map<String, Object> settings = (Map<String, Object>) taskResult.getData();
                if (settings!=null) {
                    Log.d(TAG_LOG, "LOADING PATIENT SETTINGS" + settings.toString());
                    if (settings.get(PatientEditFragment.SETTINGS_KEY.FALLEN.getKey()) != null) {
                        fallen_state_value = Boolean.parseBoolean(settings.get(PatientEditFragment.SETTINGS_KEY.FALLEN.getKey()).toString());
                    } else {
                        fallen_state_value = false;
                    }
                } else {
                    fallen_state_value = false;
                }
                setUI();
            }
        });
    }


    /***
     * Gets the patient data from the firebase and sets the counts
     */
    private void setTestCounts() {
        ArrayList<ArrayList<Double>> patientData_all = patientSession.patientData.biomarkerData.getBiomarkers();
        cleanPatientData(patientData_all);
        ArrayList<Double> accuracy_static_list = patientData_all.get(0);
        ArrayList<Double> accuracy_dynamic_list = patientData_all.get(1);
        counts_static = accuracy_static_list.size();
        counts_dynamic = accuracy_dynamic_list.size();
    }


    /***
     * Function cleans the data and remove uncesary (-1) values
     */
    private void cleanPatientData(ArrayList<ArrayList<Double>> patientData_all) {
        for (ArrayList<Double> list : patientData_all) {
            for (int i = list.size()-1; i>=0; i--) {
                if (list.get(i) == -1) {
                    list.remove(i);
                }
            }
        }
    }


    /***
     * Function Initializes UI elements
     */
    private void initializeUI() {
        tv_email = view.findViewById(R.id.tv_email_value);
        tv_email.setText(patientSession.userInfo.email);
        tv_static = view.findViewById(R.id.tv_static_tests_value);
        tv_dynamic = view.findViewById(R.id.tv_dynamic_tests_value);
        tv_fallen_state = view.findViewById(R.id.tv_fallen_state_value);
    }


    /***
     * Changes between fragments
     * @param fragment
     */
    private void switchToFragment(Fragment fragment) {
        // Set Args to new fragment
        Bundle args = new Bundle();
        args.putParcelable("patientSession", patientSession);
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