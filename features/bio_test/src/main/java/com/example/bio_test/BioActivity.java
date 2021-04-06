package com.example.bio_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;


/***
 * Galit Ilie - s1628465
 * This activity is responsible for implementing the Biometric Test Feature
 * From Here:
 *      Go to BiomarkerTests (Fragment)
 */
public class BioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);                  // Sets the View
        showFragment(new BiomarkerTests());                     // Switches to the new Fragment with test choices
    }


    /***
     * Function used to switch to a given fragment
     * @param fragment: Desired fragment to switch to
     */
    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_biomarker, fragment).commit();
    }

}