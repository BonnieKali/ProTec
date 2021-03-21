package com.example.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.session.Session;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientDashboardFragment extends Fragment {

    TextView patientTextView;

    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);

        session = Session.getInstance();

        // Sey hello to user (for testing)
        patientTextView = view.findViewById(R.id.patient_textView);
        patientTextView.setText("Hello "+session.getUser().userInfo.email);

        return view;
    }
}