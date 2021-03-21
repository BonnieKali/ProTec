package com.example.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.actions.Actions;
import com.example.session.Session;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AvatarFragment extends Fragment {
    private static final String TAG = "AvatarFragment";

    // UI
    private TextView imageCaption;

    // Logic
    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);

        session = Session.getInstance();
        imageCaption = view.findViewById(R.id.avatar_caption);

        Log.d(TAG, "User object is: "+session.getUser().toString());
        Log.d(TAG, "UserInfo object is: "+session.getUser().userInfo.toString());


        imageCaption.setText(session.getUser().userInfo.email);

        view.findViewById(R.id.button_login_toapp).setOnClickListener(v -> {
            Activity act = getActivity();
            if(act != null){
                act.startActivity(Actions.openDashboardIntent(act));
            }
        });
        return view;
    }
}