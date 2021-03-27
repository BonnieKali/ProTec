package com.example.session.user.data.biomarker;

import android.util.Log;

import java.util.ArrayList;

public class BiomarkerData {

    private static final String TAG_BIO_DATA = "TAG_BIO_DATA";
    private ArrayList<Double> accuracy_static_list;
    private ArrayList<Double> accuracy_dynamic_list;
    private ArrayList<Double> speed_static_list;
    private ArrayList<Double> speed_dynamic_list;

    /***
     * Initialize class with initial values
     * @param accuracy_static
     * @param accuracy_dynamic
     * @param speed_static
     * @param speed_dynamic
     */
    public BiomarkerData(ArrayList<Double> accuracy_static, ArrayList<Double> accuracy_dynamic, ArrayList<Double> speed_static, ArrayList<Double> speed_dynamic) {
        accuracy_static_list = new ArrayList<Double>(accuracy_static);
        accuracy_dynamic_list = new ArrayList<Double>(accuracy_dynamic);
        speed_static_list = new ArrayList<Double>(speed_static);
        speed_dynamic_list = new ArrayList<Double>(speed_dynamic);
        // -- DEBUG --
        String listString = "";
        for (double s : accuracy_dynamic_list)
        {
            listString += s + "\t";
        }
        Log.i(TAG_BIO_DATA, listString + "\n\n\n\n\n" );
        // -- DEBUG --
    }


    /***
     * Updates the values of the biomarkers
     * @param accuracy_static
     * @param accuracy_dynamic
     * @param speed_static
     * @param speed_dynamic
     */
    public void updateBiomarker(double accuracy_static, double accuracy_dynamic, double speed_static, double speed_dynamic) {
        // -- DEBUG --
        Log.i(TAG_BIO_DATA, "accuracy_static="+accuracy_static);
        String listString = "";
        for (double s : accuracy_dynamic_list)
        {
            listString += s + "\t";
        }
        Log.i(TAG_BIO_DATA, listString);
        // -- DEBUG --
        accuracy_dynamic_list.add(accuracy_dynamic);
        accuracy_static_list.add(accuracy_static);
        speed_static_list.add(speed_static);
        speed_dynamic_list.add(speed_dynamic);
    }


    /***
     * Returns an array of biomarkers
     * order: accuracy_static, accuracy_dynamic, speed_static, speed_dynamic
     * @return
     */
    public ArrayList<ArrayList<Double>> getBiomarkers(){
        ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>(4);
        data.add(accuracy_static_list);
        data.add(accuracy_dynamic_list);
        data.add(speed_static_list);
        data.add(speed_dynamic_list);
        return data;
    }
}
