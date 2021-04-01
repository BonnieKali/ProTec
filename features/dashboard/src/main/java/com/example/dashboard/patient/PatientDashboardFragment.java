package com.example.dashboard.patient;

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
import com.example.deadreckoning.DeadReckoning;
import com.example.session.Session;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientDashboardFragment extends Fragment {
    private static final String TAG = "PatientDashboardFrag";

    TextView patientTextView;

    private Session session;

    private DeadReckoning deadReckoning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);

        session = Session.getInstance();

        // Sey hello to user (for testing)
        patientTextView = view.findViewById(R.id.patient_textView);
        patientTextView.setText("Hello "+session.getUser().userInfo.email);

        // Start service for patient notifications
        startPatientService();

        // Set Listeners for clickable items
        setOnClickListeners(view);

        // Start dead reckoning
        Log.d(TAG, "Starting dead reckoning");
        deadReckoning = new DeadReckoning(getActivity());

        return view;
    }



    /**
     * custom on click listener
     * @param view view of the inflated view (all elements on screen)
     */
    private void setOnClickListeners(View view) {
        setBioTestButtonListener(view.findViewById(R.id.dashboard_to_BioTest)); // Bio Button
        setOpenMapButtonListener(view.findViewById(R.id.open_map_btn)); // Bio Button
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

    /**
     * Listener for the BioTest Button
     * @param button_toBioTest: Button
     */
    private void setBioTestButtonListener(Button button_toBioTest) {
        button_toBioTest.setOnClickListener(v -> {
            Activity act = getActivity();
            if(act != null){
                Intent intent_BioTest = Actions.openBioTestIntent(act);
                act.startActivity(intent_BioTest);
            }
        });
    }

    private void startPatientService() {
        Activity act = getActivity();
        if (act != null){
            Log.d(TAG, "Activity was not null!. Starting PatientService");
            act.startService(Actions.getPatientServiceIntent(act));
        }else{
            Log.w(TAG, "Activity was null!. Cannot start PatientService!");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        deadReckoning.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        deadReckoning.resume();
    }

}