package com.example.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Set Listeners for clickable items
        setOnClickListeners(view);

        return view;
    }


    private void setOnClickListeners(View view){
        // Set listener for sign-in click
        view.findViewById(R.id.button_login_signin).setOnClickListener(v -> {
            int action = R.id.action_loginFragment_to_avatarFragment; // action is NavDirections class normally
            Navigation.findNavController(v).navigate(action);
        });

        // Set listener for register click
        view.findViewById(R.id.register_link).setOnClickListener(v -> {
            int action = R.id.action_loginFragment_to_registerFragment;
            Navigation.findNavController(v).navigate(action);
        });

    }
}