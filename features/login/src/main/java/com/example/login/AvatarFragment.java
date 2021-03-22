package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    private final float AVATAR_DURATION_SECONDS = 4.0f;

    // Specifies delay until the explanation text and progress bar appear
    private final float EXPLANATION_DELAY_SECONDS = 1.0f;

    // UI Variables
    private TextView imageCaption;
    private TextView hello_email;
    private ProgressBar progressBar;
    private TextView progressExplanation;

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
        progressBar = view.findViewById(R.id.avatar_progress_bar);
        progressExplanation = view.findViewById(R.id.avatar_progress_explanation);

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
        // Go to next target after N seconds if the button is not clicked
        setGoTimer();
    }


    /**
     * Sets timer which takes user to dashboard
     */
    private void setGoTimer() {

        // Create background task which waits AVATAR_DURATION_SECONDS and sends empty result
        RunnableTask task_duration = () -> {
            SystemClock.sleep((long)(AVATAR_DURATION_SECONDS*1000));
            return new TaskResult<>(null);
        };

        // Create background task which waits EXPLANATION_DELAY_SECONDS and sends empty result
        RunnableTask task_delay = () -> {
            SystemClock.sleep((long)(EXPLANATION_DELAY_SECONDS*1000));
            return new TaskResult<>(null);
        };

        // Attach task and go to dashboard upon completion
        BackgroundPool.attachTask(task_duration, taskResult -> toDashboard());

        // Attach task_delay and show progress/explanation upon completion
        BackgroundPool.attachTask(task_delay, taskResult -> enableProgress());

    }


    /**
     * Takes user to dashboard, clearing the view stack
     */
    private void toDashboard(){
        Activity act = getActivity();
        if(act != null){
            Intent intent = Actions.openDashboardIntent(act);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            disableProgress();
            act.startActivity(intent);
            act.finish();
        }
    }


    /**
     * Enables progress bar and explanation text
     */
    private void enableProgress(){
        progressBar.setVisibility(View.VISIBLE);
        progressExplanation.setVisibility(View.VISIBLE);
    }

    /**
     * Disables progress bar and explanation (when dashboard is ready)
     */
    private void disableProgress(){
        progressBar.setVisibility(View.INVISIBLE);
        progressExplanation.setVisibility(View.INVISIBLE);
    }

}