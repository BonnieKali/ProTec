package com.example.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.session.Session;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set button listener
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        view.findViewById(R.id.button_login_start).setOnClickListener(v -> {
            int action = R.id.action_welcomeFragment_to_loginFragment; // action is NavDirections class normally
            Navigation.findNavController(v).navigate(action);
        });

        return view;
    }
}