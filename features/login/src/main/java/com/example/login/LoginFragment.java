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

import com.example.session.Session;
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
    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize fragment variables
        progressBar = view.findViewById(R.id.progress_bar);
        email = view.findViewById(R.id.login_email);
        password = view.findViewById(R.id.login_password);

        session = Session.getInstance();

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
        setRegisterLinkButtonListener(view.findViewById(R.id.register_link));    // Register Button`
    }


    /**
     * Set listener for sign-in click.
     * @param loginButton View object corresponding to Login button
     */
    private void setLoginButtonListener(View loginButton) {
        loginButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);     // Enable progress bar
            int to_avatarFragment = R.id.action_loginFragment_to_avatarFragment;

            session.signInUserWithEmailPassword(
                email.getText().toString(),
                password.getText().toString(),
                taskResult -> {
                    Log.d(TAG, "setLoginButtonListener: Running callback in UI");

                    if(taskResult instanceof TaskResult.Error){
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "setLoginButtonListener: Authentication error");
                        ProTecAlerts.warning(getContext(),
                                ((TaskResult.Error<?>) taskResult).exception.getMessage());
                    }
                    else {
                        Log.d(TAG, "setLoginButtonListener: Authentication result is" +
                                " successful. Leaving login screen");
                        Navigation.findNavController(v).navigate(to_avatarFragment);
                    }
                });
            });
    }


    /**
     * Sets listener for registerButton press
     * @param registerButton View object corresponding to RegisterButton
     */
    private void setRegisterLinkButtonListener(View registerButton){
        registerButton.setOnClickListener(v -> {
            int action = R.id.action_loginFragment_to_registerFragment;
            Navigation.findNavController(v).navigate(action);
        });
    }

}