package com.example.dashboard.recycler;

import android.view.View;
import android.widget.FrameLayout;

import com.example.dashboard.R;
import com.example.session.user.patient.PatientSession;

import java.util.ArrayList;
import java.util.List;

public class PatientItem {

    private int mImageResource;
    private String mText1;
    private String mText2;
    private boolean belongToCarer;
    private String ID;

    public PatientItem(int mImageResource, String mText1, String mText2, boolean belongToCarer, String ID){
        this.mImageResource = mImageResource;
        this.mText1 = mText1;
        this.mText2 = mText2;
        this.belongToCarer = belongToCarer;
        this.ID = ID;
    }



    // -- Getters -- //
    public int getmImageResource() {
        return mImageResource;
    }

    public String getmText1() {
        return mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public boolean isBelongToCarer() {
        return belongToCarer;
    }

    public String getID() {
        return ID;
    }

    // --------------

    // -- Setters -- //

    public void setmImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public void setBelongToCarer(boolean belongToCarer) {
        this.belongToCarer = belongToCarer;
    }
    // ----------------


    public static ArrayList<PatientItem> initialisePatients(View view, List<PatientSession> patientSessions){
        ArrayList<PatientItem> patientItems = new ArrayList<>();
        FrameLayout cardBackground = view.findViewById(R.id.patient_card_frame_layout);
        for (PatientSession patient : patientSessions){
            String name = patient.userInfo.getUserName();
            String bio_data = patient.patientData.biomarkerData.getBiomarkers().toString();
            String ID = patient.getUID();
            patientItems.add(new PatientItem(R.drawable.ic_person, name,bio_data, true, ID));
//            cardBackground.setBackgroundColor(view.getResources().getColor(R.color.light_green));
        }
        return patientItems;
    }
}
