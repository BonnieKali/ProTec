package com.example.dashboard.recycler;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dashboard.R;
import com.example.session.user.patient.PatientSession;
import com.google.android.material.slider.Slider;

import java.util.Map;

/**
 * create an instance of this fragment.
 */
public class PatientEditFragment extends Fragment {
    // Log
    String TAG_LOG =  "PatientEditFragment";
    // Patient
    PatientSession patientSession;
    // Class
    View view;
    // Ui
    TextView tv_acc, tv_q1, tv_q2;
    Slider slider_acc, slider_q1, slider_q3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_patient_edit, container, false);
        // Ext
        // Initialize UI
        initializeUi();
        return view;
    }


    /***
     * Function Initializes the Ui Elements
     */
    private void initializeUi() {
        tv_acc = view.findViewById(R.id.tv_slider_thresh_acc_value);
        tv_q1 = view.findViewById(R.id.tv_slider_thresh_q1_value);
        tv_q2 = view.findViewById(R.id.tv_slider_thresh_q2_value);
    }


//session.getPatientSettings(patientSession.userInfo.id, new OnTaskCompleteCallback() {
//@Override
//public void onComplete(TaskResult<?> taskResult) {
//    Map<String, Object> settings = (Map<String, Object>) taskResult.getData();
//    Log.d(TAG,settings.toString());
//}
//});
//session.setPatientSetting(patientSession.userInfo.id,"James",true);
}