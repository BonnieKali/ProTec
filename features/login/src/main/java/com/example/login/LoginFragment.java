package com.example.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.session.local.UserSession;
import com.example.session.database.Database;
import com.example.threads.TaskResult;
import com.example.ui.ProTecAlerts;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    // UI
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;

    // Logic
    private UserSession session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize fragment variables
        progressBar = view.findViewById(R.id.progress_bar);
        email = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);

        session = UserSession.getInstance();

        // Set Listeners for clickable items
        setOnClickListeners(view);

        runTests();

        return view;
    }

    /*
    TESTING PURPOSES
     */
    private void runTests() {
        Database.init();
        Database.test();
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
            progressBar.setVisibility(View.VISIBLE);     // Enable progress bar
            int action = R.id.action_loginFragment_to_avatarFragment;

            session.signInUserWithEmailPassword(
                email.getText().toString(),
                password.getText().toString(),
                taskResult -> {
                    Log.d(TAG, "OnTaskCompleteCallback: Running callback in UI");

                    if(taskResult instanceof TaskResult.Error){
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "OnTaskCompleteCallback: Authentication error");
                        ProTecAlerts.warning(getContext(),
                                ((TaskResult.Error<?>) taskResult).exception.getMessage());
                    }
                    else {
                        Log.d(TAG, "OnTaskCompleteCallback: Authentication result is" +
                                " successful. Leaving login screen");
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
            int action = R.id.action_loginFragment_to_registerFragment;
            Navigation.findNavController(v).navigate(action);
        });
    }

}