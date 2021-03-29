package com.example.dashboard.recycler;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.patient.PatientSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PatientItem {

    private int mImageResource;
    private String mText1;
    private String mText2;
    private boolean belongToCarer;
    private String ID;
    private PatientSession session;

    public PatientItem(int mImageResource, PatientSession session){
        this.mImageResource = mImageResource;
        this.mText1 = session.userInfo.getUserName();
        this.mText2 = session.userInfo.email;
//        this.belongToCarer = belongToCarer;
        this.ID = session.getUID();
        this.session = session;
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

    /**
     * Checks if the given carer string belongs to a carer by looking
     * at the patients carers
     * @param ID: carers ID
     * @return
     */
    public boolean isBelongToCarer(String ID) {
        //TODO check the localLiveData for this instead of here
        return session.patientData.isAssignedCarer(ID);
    }

    public String getID() {
        return ID;
    }

    public PatientSession getSession(){
        return this.session;
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


    public static ArrayList<PatientItem> initialisePatients(View view, HashSet<PatientSession> patientSessions){
        ArrayList<PatientItem> patientItems = new ArrayList<>();
        Log.d("PatientItem","Patient Sessions: " + patientSessions);
        for (PatientSession patient : patientSessions){
            patientItems.add(new PatientItem(R.drawable.ic_person, patient));
        }
        return patientItems;
    }
}
