package com.example.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.actions.Actions;
import com.example.dashboard.recycler.PatientAdapter;
import com.example.dashboard.recycler.PatientItem;
import com.example.session.Session;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarerDashboardFragment extends Fragment {
    private static final String TAG = "CarerDashboardFragment";

    private RecyclerView mRecyclerView; // shows patients
    private PatientAdapter mAdapter;  //deals with loading items to recyclerviewer
    private RecyclerView.LayoutManager mLayoutManager;  // sets up the list viewer

    ArrayList<PatientItem> patientItems;

    TextView carerTextView;

    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carer_dashboard, container, false);

        session = Session.getInstance();

        // Say hello to user (for testing
        carerTextView = view.findViewById(R.id.carer_textView);
        carerTextView.setText("Hello "+session.getUser().userInfo.email);

        // Start service for notifications
        startNotificationService();
        // Set Listeners for clickable items
        setOnClickListeners(view);

        initialisePatientView(view);

        return view;
    }

    /**
     * Initialise the Patient Recycler Viewer
     * @param view
     */
    private void initialisePatientView(View view) {
        patientItems = new ArrayList<>();
        patientItems.add(new PatientItem(R.drawable.ic_person, "Ilie","is Gay"));
        patientItems.add(new PatientItem(R.drawable.ic_person, "Hamesz","is Straight"));

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new PatientAdapter(patientItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        // se this on item click listener for when someone touches the patient list
        mAdapter.setOnItemClickListener(new PatientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeItem(position);
            }

            @Override
            public void onPatientAddClick(int position, View v) {
                addPatient(position, v);
            }

            @Override
            public void onPatientRemoveClick(int position, View v) {
                removePatient(position, v);
            }
        });
    }

    /**
     * Removes the patient from the carers patients list
     * @param position
     * @param v
     */
    private void removePatient(int position, View v) {
        // set + visibility true
        ImageView plus = (ImageView) v.findViewById(R.id.image_patient_add);
        plus.setVisibility(v.VISIBLE);
        // set - unvisible
        ImageView remove = (ImageView) v.findViewById(R.id.image_patient_remove);
        remove.setVisibility(v.INVISIBLE);
        Toast.makeText(getContext(),"Patient Removed",Toast.LENGTH_SHORT).show();
        // change the background
        FrameLayout cardBackground = (FrameLayout) v.findViewById(R.id.patient_card_frame_layout);
        cardBackground.setBackgroundColor(getResources().getColor(R.color.light_red));
    }

    /**
     * Adds the patient to the careers list of patients
     * @param position
     * @param v
     */
    private void addPatient(int position, View v) {
        // set + visibility true
        ImageView remove = (ImageView) v.findViewById(R.id.image_patient_remove);
        remove.setVisibility(v.VISIBLE);
        // set - unvisible
        ImageView plus = (ImageView) v.findViewById(R.id.image_patient_add);
        plus.setVisibility(v.INVISIBLE);
        Toast.makeText(getContext(),"Patient Added",Toast.LENGTH_SHORT).show();
        // change the background
        FrameLayout cardBackground = (FrameLayout) v.findViewById(R.id.patient_card_frame_layout);
        cardBackground.setBackgroundColor(getResources().getColor(R.color.light_green));
    }

    /**
     * This method deals with changing the patientItem data and updates
     * the recycler viewer displaying it
     * @param position
     */
    private void changeItem(int position) {
        patientItems.get(position).setmText2("Ilie is super gay");
        mAdapter.notifyItemChanged(position);
    }

    /**
     * custom on click listener
     * @param view view of the inflated view (all elements on screen)
     */
    private void setOnClickListeners(View view) {
        setOpenMapButtonListener(view.findViewById(R.id.open_map_btn)); // Map Button
    }

    /**
     * Sets up what happens when the button is clicked and opens the
     * map
     * @param button_open_map
     */
    private void setOpenMapButtonListener(Button button_open_map) {
        button_open_map.setOnClickListener(v -> {
            Activity act = getActivity();
            if(act != null){
                Intent intent = Actions.openMapIntent(act);
                act.startActivity(intent);
            }
        });
    }


    /**
     * Starts the notification service in the background
     */
    private void startNotificationService() {
        Activity act = getActivity();
        if (act != null){
            Log.d(TAG, "Activity was not null!. Starting notification service");
            act.startService(Actions.getNotificationServiceIntent(act));
        }else{
            Log.w(TAG, "Activity was null!. Cannot start service!");
        }
    }
}