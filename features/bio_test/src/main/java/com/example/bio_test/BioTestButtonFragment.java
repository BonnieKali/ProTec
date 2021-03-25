package com.example.bio_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.actions.Actions;
import com.example.session.Session;
import com.example.session.event.Event;
import com.example.session.event.EventType;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.patient.PatientSession;

/**
 * create an instance of this fragment.
 */
public class BioTestButtonFragment extends Fragment {

    private Session session;
    private PatientSession patientSession;
    private UserSession userSession;
    private TextView tv_patientID_counter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bio_test_button, container, false);

        // get the session
        session = Session.getInstance();
        userSession =  session.getUser();
        if(userSession.userInfo.userType == UserInfo.UserType.PATIENT) {
            patientSession = (PatientSession) userSession;
        }
        tv_patientID_counter = view.findViewById(R.id.textView_patientId_bioCounter);
        setOnClickListeners(view);
        return view;
    }

    /**
     * custom on click listener
     * @param view view of the inflated view (all elements on screen)
     */
    private void setOnClickListeners(View view) {
        setBioTestButtonListener(view.findViewById(R.id.button_bioCounter)); // Bio Button
    }

    /**
     * Listener for the BioTest Button
     * @param button_toBioTest: Button
     */
    private void setBioTestButtonListener(Button button_toBioTest) {
        button_toBioTest.setOnClickListener(v -> {
            int past_value = patientSession.patientData.biomarkerData.get_buttonPressCounter();
            if (past_value%20==0){          // spawn an events
                session.generateLiveEvent(EventType.FELL);
            }
            past_value += 1;
            patientSession.patientData.biomarkerData.updateCounter(past_value);
            updateTextViewCounter(past_value);
        });
    }

    private void updateTextViewCounter(int past_value) {
        String user_name = patientSession.userInfo.getUserName();
        tv_patientID_counter.setText(user_name + ": " + past_value);
    }
}