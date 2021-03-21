package com.example.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.session.Session;
import com.example.session.user.UserInfo;
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
    private UserInfo.UserType userType;


    // Logic
    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Retrieve session
        session = Session.getInstance();

        // Initialize fragment views
        progressBar = view.findViewById(R.id.progress_bar_register);
        email = view.findViewById(R.id.register_email);
        password = view.findViewById(R.id.register_password);

        // Set Listeners for clickable items
        setOnClickListeners(view);

        return view;
    }


    /**
     * Responsible for setting all button listeners.
     * @param view Current fragment view
     */
    private void setOnClickListeners(View view) {
        // Register Button
        setRegisterButtonListener(view.findViewById(R.id.button_register_register));

        // UserType spinner
        setUserTypeSpinnerListener(view.findViewById(R.id.user_type_dropdown));
    }


    /**
     * Sets listener for register button.
     * @param registerButton Button View
     */
    private void setRegisterButtonListener(View registerButton) {
        // Define button navigation action
        int to_avatarFragment = R.id.action_registerFragment_to_avatarFragment;

        // Set listener
        registerButton.setOnClickListener(v -> {
            // Log parameters on Click
            Log.d(TAG, "setRegisterButtonListener: Clicked with parameters: " +
                    email.getText().toString() + " " +
                    password.getText().toString() + " " +
                    userType);

            // Enable loading arrow in UI thread
            progressBar.setVisibility(View.VISIBLE);

            // Attempt to register user details and define a UI callback to deal with the result.
            session.createUserWithEmailPassword(
                    email.getText().toString(),
                    password.getText().toString(),
                    userType,
                    taskResult -> {
                        Log.d(TAG, "setRegisterButtonListener: Running callback in UI");

                        // If there was an error, display it to the user
                        if(taskResult instanceof TaskResult.Error){
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "setRegisterButtonListener: Authentication error");
                            ProTecAlerts.warning(getContext(),
                                    ((TaskResult.Error<?>) taskResult).exception.getMessage());
                        }
                        // If the result is successful, go to the Avatar fragment
                        else {
                            Log.d(TAG, "setRegisterButtonListener: Authentication result is" +
                                    " successful. Leaving login screen");
                            Navigation.findNavController(v).navigate(to_avatarFragment);
                        }
                    });
        });
    }


    /**
     * Sets Listener for the UserType dropdown menu
     *
     * @param spinner Spinner View
     */
    private void setUserTypeSpinnerListener(View spinner) {
        Spinner userTypeSpinner = (Spinner) spinner;

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.account_types, R.layout.usertype_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        userTypeSpinner.setAdapter(spinnerAdapter);

        // Set listener for spinner choices (default should be patient) and define callback action
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getItemAtPosition(position).toString();

                if ("Carer".equals(choice)) {
                    userType = UserInfo.UserType.CARER;
                } else {
                    userType = UserInfo.UserType.PATIENT;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



    }




}