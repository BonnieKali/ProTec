package com.example.dashboard.recycler;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.patient.PatientSession;

/**
 * create an instance of this fragment.
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
       view = inflater.inflate(R.layout.fragment_patient_info, container, false);      // Inflate the layout for this fragment
       patientSession = getArguments().getParcelable("patientSession");                        // get user id
       setOnClickListeners(view);                                                                   // set listeners
       return view;
    }


    /**
     * custom on click listener
     * @param
     */
    private void setOnClickListeners(View view) {
        setButtonContactListener(view.findViewById(R.id.btn_contact));
        setButtonDataListener(view.findViewById(R.id.btn_data));
        setButtonEditListener(view.findViewById(R.id.btn_edit));
    }

    private void setButtonEditListener(Button button) {
        button.setOnClickListener(v -> {
            PatientEditFragment patientEditFragment = new PatientEditFragment();
            switchToFragment(patientEditFragment);
        });
    }

    private void setButtonDataListener(Button button) {
        button.setOnClickListener(v -> {
            PatientDataFragment patientDataFragment = new PatientDataFragment();
            switchToFragment(patientDataFragment);
        });
    }

    private void setButtonContactListener(Button button) {
        button.setOnClickListener(v -> {
//            Session.getInstance().generateLivePatientNotification(patientSession.getUID(),"TITLE","MESSAGE");
            PatientContactFragment patientContactFragment = new PatientContactFragment();
            switchToFragment(patientContactFragment);
        });
    }

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