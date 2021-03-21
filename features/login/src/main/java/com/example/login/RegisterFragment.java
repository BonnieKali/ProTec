package com.example.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.session.Session;
import com.example.threads.TaskResult;
import com.example.ui.ProTecAlerts;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";

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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize fragment variables
        progressBar = view.findViewById(R.id.progress_bar_register);
        email = view.findViewById(R.id.register_email);
        password = view.findViewById(R.id.register_password);

        session = Session.getInstance();

        // Set Listeners for clickable items
        setOnClickListeners(view);

        return view;
    }

    private void setOnClickListeners(View view) {
        // Register Button
        setRegisterButtonListener(view.findViewById(R.id.button_register_register));
    }

    private void setRegisterButtonListener(View registerButton) {
        registerButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);     // Enable progress bar
            int to_avatarFragment = R.id.action_registerFragment_to_avatarFragment;

            session.createUserWithEmailPassword(
                    email.getText().toString(),
                    password.getText().toString(),
                    taskResult -> {
                        Log.d(TAG, "setRegisterButtonListener: Running callback in UI");

                        if(taskResult instanceof TaskResult.Error){
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "setRegisterButtonListener: Authentication error");
                            ProTecAlerts.warning(getContext(),
                                    ((TaskResult.Error<?>) taskResult).exception.getMessage());
                        }
                        else {
                            Log.d(TAG, "setRegisterButtonListener: Authentication result is" +
                                    " successful. Leaving login screen");
                            Navigation.findNavController(v).navigate(to_avatarFragment);
                        }
                    });
        });

    }
}