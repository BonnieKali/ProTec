package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.actions.Actions;
import com.example.session.Session;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AvatarFragment extends Fragment {
    private static final String TAG = "AvatarFragment";

    // Specifies duration for the life of the current fragment
    private final float AVATAR_DURATION_SECONDS = 2.0f;

    // UI Variables
    private TextView imageCaption;
    private TextView hello_email;

    // Logic
    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);

        // Retrieve Session
        session = Session.getInstance();

        // Retrieve UI
        imageCaption = view.findViewById(R.id.avatar_caption);
        hello_email = view.findViewById(R.id.avatar_email);

        // Log user details
        Log.d(TAG, "User object is: "+session.getUser().toString());

        // Set UI text
        imageCaption.setText(session.getUser().userInfo.email);
        hello_email.setText(session.getUser().userInfo.getUserName());

        // Set action listener
        setListeners(view);

        return view;
    }


    /**
     * Sets listeners
     *
     * @param view Current inflated view
     */
    private void setListeners(View view){

        // This is used when we want the user to click the button to go to dashboard
        // Currently this happens automatically after AVATAR_DURATION_SECONDS
        /*
        // Set listener to button. This takes the user to the dashboard.
        view.findViewById(R.id.button_login_toapp).setOnClickListener(v -> {
            Activity act = getActivity();
            if(act != null){
                act.startActivity(Actions.openDashboardIntent(act));
            }
        });
        */

        // Go to next target after N seconds if the button is not clicked
        setGoTimer();
    }


    /**
     * Sets timer which takes user to dashboard
     */
    private void setGoTimer() {

        // Create background task which waits AVATAR_DURATION_SECONDS and sends an result
        RunnableTask task = () -> {
            SystemClock.sleep((long)(AVATAR_DURATION_SECONDS*1000));
            return new TaskResult<>(null);
        };

        // Attach task and go to dashboard upon completion
        BackgroundPool.attachTask(task, taskResult -> toDashboard());
    }


    /**
     * Takes user to dashboard, clearing the view stack
     */
    private void toDashboard(){
        Activity act = getActivity();
        if(act != null){
            Intent intent = Actions.openDashboardIntent(act);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            act.startActivity(intent);
        }
    }

}