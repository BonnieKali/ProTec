package com.example.dashboard.recycler;

public class PatientItem {

    private int mImageResource;
    private int removePatientImageResource;
    private int addPatientImageResource;
    private String mText1;
    private String mText2;

    public PatientItem(int mImageResource, String mText1, String mText2){
        this.mImageResource = mImageResource;
        this.mText1 = mText1;
        this.mText2 = mText2;
    }
    public PatientItem(int mImageResource, String mText1, String mText2, int removePatientImageResource, int addPatientImageResource){
        this.mImageResource = mImageResource;
        this.mText1 = mText1;
        this.mText2 = mText2;
        this.addPatientImageResource = addPatientImageResource;
        this.removePatientImageResource = removePatientImageResource;
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

    // ----------------
}
