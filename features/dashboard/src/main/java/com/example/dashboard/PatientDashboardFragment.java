package com.example.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.session.Session;
import com.example.session.event.Event;
import com.example.session.event.EventType;
import com.example.session.user.UserInfo;
import com.example.session.user.patient.PatientData;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;
import com.example.ui.ProTecAlerts;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientDashboardFragment extends Fragment {

    private TextView patientTextView;
    private Button generateFallEvent;
    private Button generateLeftEvent;


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

        generateFallEvent = view.findViewById(R.id.generate_fall_event);
        generateLeftEvent = view.findViewById(R.id.generate_left_house_event);
        generateFallEvent.setOnClickListener(v -> session.generateLiveEvent(EventType.FELL));
        generateLeftEvent.setOnClickListener(v -> session.generateLiveEvent(EventType.LEFT_HOUSE));

        doExamples();

        return view;
    }

    private void doExamples() {

        PatientSession patientSession = (PatientSession) session.getUser();

        String id = patientSession.userInfo.id;
        String email = patientSession.userInfo.email;
        UserInfo.UserType userType = patientSession.userInfo.userType;

        PatientData patientData = patientSession.patientData;


        // Do heavy calculation
        RunnableTask heavy_task = () -> {
            // WE DO INTENSE SHIT
            SystemClock.sleep(4*1000);
            int result = 150;
            return new TaskResult<Integer>(result);
        };

        // Deal with result of calculation
        OnTaskCompleteCallback uiCallback = taskResult -> {
            Integer result = (Integer) taskResult.getData();
//            ((PatientSession)session.getUser()).patientData.reconstructionData.sensor_data = result; // Same as below
            patientSession.patientData.reconstructionData.sensor_data = result;
//            patientSession.patientData.locationData // James' data
            ProTecAlerts.warning(getActivity(), "the data has returned as: "+result);
            session.saveState();
        };

        // Send it to thread pool to work
        BackgroundPool.attachTask(heavy_task, uiCallback);

    }
}