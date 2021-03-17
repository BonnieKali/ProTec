package com.example.login;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.session.UserSession;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

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


    private void setOnClickListeners(View view){
        // Set listener for sign-in click
        view.findViewById(R.id.button_login_signin).setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            int action = R.id.action_loginFragment_to_avatarFragment;
//            session.signInUserWithEmailPassword(getContext(), "patient0@protec.com", "123456");
            Navigation.findNavController(v).navigate(action);
        });

        // Set listener for register click
        view.findViewById(R.id.register_link).setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            int action = R.id.action_loginFragment_to_registerFragment;
            Navigation.findNavController(v).navigate(action);
        });

    }
}