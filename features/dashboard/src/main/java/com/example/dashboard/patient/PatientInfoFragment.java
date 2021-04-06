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
import android.widget.TextView;

import com.example.dashboard.R;
import com.example.session.user.patient.PatientSession;

/**
 * Ilie Galit s1628465
 * Fragment that displays the Patent View to the Carer
 * Here the carer can see more information to the patient, view data or edit the patient
 * From here can go to:
 *      PatientDataFragment
 *      PatientContactFragment
 *      PatientEditFragment
 */
public class PatientInfoFragment extends Fragment {
    // Logging
    String TAG_LOG = "PatientInfoFragment";
    // Class Vars
    View view;
    // Patient Variables
    PatientSession patientSession;


   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_patient_info, container, false);       // Inflate the layout for this fragment
       patientSession = getArguments().getParcelable("patientSession");                        // get user id
       ((TextView)view.findViewById(R.id.patient_textView)).
               setText(patientSession.userInfo.getUserName());                                      // set user name on ui
       setOnClickListeners(view);                                                                   // set listeners
       return view;
    }


    /**
     * Setup Listeners for the 3 buttons
     * @param
     */
    private void setOnClickListeners(View view) {
        setButtonContactListener(view.findViewById(R.id.btn_contact));              // Go to PatientContactFragment
        setButtonDataListener(view.findViewById(R.id.btn_data));                    // Go to PatientDataFragment
        setButtonEditListener(view.findViewById(R.id.btn_edit));                    // Go to PatientEditFragment
    }

    /***
     * Listener for the Edit button
     * @param button
     */
    private void setButtonEditListener(Button button) {
        button.setOnClickListener(v -> {
            PatientEditFragment patientEditFragment = new PatientEditFragment();                // Go to PatientEditFragment
            switchToFragment(patientEditFragment);
        });
    }

    /***
     * Listener for the Data button
     * @param button
     */
    private void setButtonDataListener(Button button) {
        button.setOnClickListener(v -> {
            PatientDataFragment patientDataFragment = new PatientDataFragment();                // Go to PatientDataFragment
            switchToFragment(patientDataFragment);
        });
    }

    /***
     * Listener for the Contact button
     * @param button
     */
    private void setButtonContactListener(Button button) {
        button.setOnClickListener(v -> {
            PatientContactFragment patientContactFragment = new PatientContactFragment();       // Go to PatientContactFragment
            switchToFragment(patientContactFragment);
        });
    }


    /***
     * Function that switches to a given fragment and sets the patient session as the argument
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
        transaction.replace(R.id.fragment_container, fragment); // layout_body_patient_info, fragment_container
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}