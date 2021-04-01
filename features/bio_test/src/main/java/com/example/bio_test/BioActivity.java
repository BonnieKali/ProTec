package com.example.bio_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class BioActivity extends AppCompatActivity {

    FallDetector fall_detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);                  // content view
        showFragment(new BiomarkerTests());                     // show fragment
//        showFragment(new FallDetectorFragment());
    }


    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_biomarker, fragment).commit();

    }

//    private void showFragmentFallDetector(){
//        FallDetectorFragment fragment = new FallDetectorFragment();
//        Bundle bundle = new Bundle();
//        fragment.setArguments(bundle);
//    }

}