package com.example.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This fragment welcomes the user to the ProTec and sends them to the login screen.
 */


public class WelcomeFragment extends Fragment {
    private static final String TAG = "WelcomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        Log.d(TAG, "onCreateView: Starting");

        // Set button listener to go to login screen
        view.findViewById(R.id.button_login_start).setOnClickListener(v -> {
            int action = R.id.action_welcomeFragment_to_loginFragment; // action is NavDirections class normally
            Navigation.findNavController(v).navigate(action);
        });

        return view;
    }
}