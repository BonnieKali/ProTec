package com.example.dashboard.patient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dashboard.R;
import com.example.session.user.patient.PatientSession;

import java.util.ArrayList;

/**
 * create an instance of this fragment.
 */
public class PatientTrackingDayFragment extends Fragment {
        View view;
        PatientSession patientSession;
        TextView tv_day;
       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_patient_tracking_day, container, false);
       initUI();
       setPatientInfo();
        return view;
    }


    /***
     * Initialzie UI
     */
    private void initUI() {
        tv_day = view.findViewById(R.id.tv_day_id);
    }

    /***
     * Function Deals setting patient information variables
     */
    public void setPatientInfo(){
        patientSession = getArguments().getParcelable("patientSession");
        // get other stuff from patietn session here
        String day_id = getArguments().getString("day_id");
        tv_day.setText(day_id);
        // extract data after
    }

    /***
     * EXAMPLE OF EXTRACITNG DATA
     */
    private void extractPatientData() {
        ArrayList<ArrayList<Double>> patientData_all = patientSession.patientData.biomarkerData.getBiomarkers();
    }

}