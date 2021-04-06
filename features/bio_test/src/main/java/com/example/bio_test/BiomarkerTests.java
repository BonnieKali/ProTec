package com.example.bio_test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Ilie Galit - s1628465
 * Fragment used for choosing tests
 * Presents the choice between the Static and Dynamic Tets
 * Responsible to set up correct arguments to pass to the next Fragment
 * From Here:
 *      Do Dynamic Test (BioTestButtonFragment)
 *      Do Static Test  (BioTestButtonFragment)
 */
public class BiomarkerTests extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_biomarker_tests,
                container, false);
        setOnClickListeners(view);                                                      // Set up Listeners
        return view;
    }


    /**
     * Sets up the Button Listeners
     * @param
     */
    private void setOnClickListeners(View view) {
        setButtonDynamicTestListener(view.findViewById(R.id.button_dynamic_test));      // Dynamic Test button
        setButtonStaticTestListener(view.findViewById(R.id.button_stationary_test));    // Static Test button
    }


    /***
     * Listener fot the Dynamic Test Button
     * Once clicked will switch to Fragment with Dynamic Parameters in place
     * @param button
     */
    private void setButtonDynamicTestListener(Button button) {
        button.setOnClickListener(v -> {
            BioTestButtonFragment fragment_test_static = getFragment("DYNAMIC");    // Create new fragment
            switchToFragment(fragment_test_static);                                     // switch to new fragment
        });
    }


    /***
     * Listener fot the Dynamic Test Button
     * Once clicked will switch to Fragment with Static  Parameters in place
     * @param button
     */
    private void setButtonStaticTestListener(Button button) {
        button.setOnClickListener(v -> {
            BioTestButtonFragment fragment_test_static = getFragment("STATIC");     // Create new fragment
            switchToFragment(fragment_test_static);                                     // switch to new fragment
        });
    }


    /***
     * Getter for Fragment with given argument
     * @param str Argument denoting type of test
     * @return
     */
    private BioTestButtonFragment getFragment(String str) {
        BioTestButtonFragment fragment_test = new BioTestButtonFragment();
        Bundle args = new Bundle();
        args.putString("ARG1", str);
        fragment_test.setArguments(args);
        return fragment_test;
    }

    /***
     * Function that deals with switching to a given fragment
     * @param fragment_test
     */
    private void switchToFragment(Fragment fragment_test) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container_biomarker, fragment_test);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }
}