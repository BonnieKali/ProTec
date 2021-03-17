package com.example.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.actions.Actions;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AvatarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);
        view.findViewById(R.id.button_login_toapp).setOnClickListener(v -> {
            Activity act = getActivity();
            if(act != null){
                act.startActivity(Actions.openDashboardIntent(act));
            }
        });
        return view;
    }
}