package com.example.dashboard;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.actions.Actions;
import com.example.session.Session;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarerDashboardFragment extends Fragment {
    private static final String TAG = "CarerDashboardFragment";

    TextView carerTextView;

    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carer_dashboard, container, false);

        session = Session.getInstance();

        // Say hello to user (for testing
        carerTextView = view.findViewById(R.id.carer_textView);
        carerTextView.setText("Hello "+session.getUser().userInfo.email);

        // Start service for notifications
        startNotificationService();
        return view;
    }

    /**
     * Starts the notification service in the background
     */
    private void startNotificationService() {
        Activity act = getActivity();
        if (act != null){
            Log.d(TAG, "Activity was not null!. Starting notification service");
            act.startService(Actions.getNotificationServiceIntent(act));
        }else{
            Log.w(TAG, "Activity was null!. Cannot start service!");
        }
    }
}