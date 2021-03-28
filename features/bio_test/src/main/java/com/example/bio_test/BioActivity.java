package com.example.bio_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BioActivity extends AppCompatActivity {

    SensorAccelerometer sensor_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);                  // content view
        sensor_acc = new SensorAccelerometer( this );   // getting the sensor
        showFragment(new BiomarkerTests());                     // show fragment
    }


    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_biomarker, fragment).commit();
    }


}