package com.example.bio_test;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for the fall detection
 */
public class FallDetectorFragment extends Fragment {
    FallDetector fall_detector;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fall_detector, container, false);
        // Fall detector object
        Context context = getContext();
        fall_detector = new FallDetector(context, view);       // getting the sensor
        return view;
    }
}