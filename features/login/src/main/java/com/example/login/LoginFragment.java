package com.example.login;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.session.UserSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    // UI
    private ProgressBar progressBar;

    // Logic
    private UserSession session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Init session
        progressBar = view.findViewById(R.id.progress_bar);
        session = UserSession.getInstance();

        // Set Listeners for clickable items
        setOnClickListeners(view);

        return view;
    }


    /**
     * Sets all onClick listeners inside the current fragment view
     * @param view Inflated fragment view
     */
    private void setOnClickListeners(View view) {
        setLoginButtonListener(view.findViewById(R.id.button_login_signin)); // Login Button
        setRegisterButtonListener(view.findViewById(R.id.register_link));    // Register Button
    }


    /**
     * Set listener for sign-in click.
     * @param loginButton View object corresponding to Login button
     */
    private void setLoginButtonListener(View loginButton) {
        loginButton.setOnClickListener(v -> {
            // Enable progress bar
            progressBar.setVisibility(View.VISIBLE);
            int action = R.id.action_loginFragment_to_avatarFragment;

            // Create a background authentication task
            RunnableTask task = () -> {
                // This is a blocking statement
                Boolean result = session.signInUserWithEmailPassword(getContext(),
                        "patient0@protec.com", "123456");
                return new TaskResult<>(result);
            };

            // Send the task to the ThreadPool and attach a UI callback for when it is finished
            BackgroundPool.attachTask(task, taskResult -> {
                Log.d(TAG, "OnTaskCompleteCallback: Running callback in UI");

                if (taskResult instanceof TaskResult.Error){
                    Log.d(TAG, "OnTaskCompleteCallback: Result is of type TaskResult.Error");
                }

                else if (taskResult.getData() == Boolean.TRUE) {
                    Log.d(TAG, "OnTaskCompleteCallback: Authentication result is successful. Leaving login screen");
                    Navigation.findNavController(v).navigate(action);
                }
            });

        });
    }


    /**
     * Sets listener for registerButton press
     * @param registerButton View object corresponding to RegisterButton
     */
    private void setRegisterButtonListener(View registerButton){
        registerButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            int action = R.id.action_loginFragment_to_registerFragment;
            Navigation.findNavController(v).navigate(action);
        });
    }

}