package com.example.dashboard.patient;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dashboard.DashboardActivity;
import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.patient.PatientSession;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Fragmetn for the patient Data
 */
public class PatientDataFragment extends Fragment {
    // Log
    String TAG_LOG = "PatientDataFragment";
    // Class
    View view;
    // Patient
    PatientSession patientSession;
    //chart
    private LineChart mChart_speed;
    private LineChart mChart_accuracy;
    // Data Variables
    private ArrayList<ArrayList<Double>> patientData_all;
    private ArrayList<Double> accuracy_static_list;
    private ArrayList<Double> accuracy_dynamic_list;
    private ArrayList<Double> speed_static_list;
    private ArrayList<Double> speed_dynamic_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_patient_data, container, false);
        setPatientInfo();                                                                           // Args
        // Charts
        initializeCharts();
        // plot data
        plotData();
        return view;
    }


    /***
     * Function Deals with plotting the data  on the charts
     */
    private void plotData() {
        plotAccuracyData();
        plotSpeedData();
    }


    /***
     * Function sets up and plots the accuracy Data
     */
    private void plotAccuracyData() {
        ArrayList<Entry> entry_static = new ArrayList<>();
        ArrayList<Entry> entry_dynamic = new ArrayList<>();
        for (int i = 0; i < accuracy_static_list.size(); i++){
            entry_static.add(new Entry(i,accuracy_static_list.get(i).floatValue()));
        }
        for (int i = 0; i < accuracy_dynamic_list.size(); i++){
            entry_dynamic.add(new Entry(i,accuracy_dynamic_list.get(i).floatValue()));
        }
        // put in the data set
        LineDataSet set_static = new LineDataSet(entry_static,"Static Accuracy");
        LineDataSet set_dynamic = new LineDataSet(entry_dynamic,"Dynamic Accuracy");
        // Set color and alpha
        set_static.setFillAlpha(200);set_dynamic.setFillAlpha(200);
        set_static.setColor(Color.BLUE);set_static.setCircleColor(Color.BLUE);
        set_dynamic.setColor(Color.RED);set_dynamic.setCircleColor(Color.RED);
        // add to line data
        LineData lineData = new LineData(set_static,set_dynamic);
        // plot on chart
        mChart_accuracy.setData(lineData);
    }


    /***
     * Function sets up and plots the speed Data
     */
    private void plotSpeedData() {
        ArrayList<Entry> entry_static = new ArrayList<>();
        ArrayList<Entry> entry_dynamic = new ArrayList<>();
        for (int i = 0; i < speed_static_list.size(); i++){
            entry_static.add(new Entry(i,speed_static_list.get(i).floatValue()));
        }
        for (int i = 0; i < speed_dynamic_list.size(); i++){
            entry_dynamic.add(new Entry(i,speed_dynamic_list.get(i).floatValue()));
        }
        // put in the data set
        LineDataSet set_static = new LineDataSet(entry_static,"Static Speed");
        LineDataSet set_dynamic = new LineDataSet(entry_dynamic,"Dynamic Speed");
        // Set color and alpha
        set_static.setFillAlpha(200);set_dynamic.setFillAlpha(200);
        set_static.setColor(Color.BLUE);set_static.setCircleColor(Color.BLUE);
        set_dynamic.setColor(Color.RED);set_dynamic.setCircleColor(Color.RED);
        // add to line data
        LineData lineData = new LineData(set_static,set_dynamic);
        // plot on chart
        mChart_speed.setData(lineData);
    }


    /***
     * Initializes the Charts
     */
    private void initializeCharts() {
        mChart_speed = (LineChart) view.findViewById(R.id.lineChart_speed);
        mChart_accuracy = (LineChart) view.findViewById(R.id.lineChart_accuracy);
        mChart_speed.setDragEnabled(false); mChart_speed.setDragEnabled(true);
        mChart_accuracy.setDragEnabled(false); mChart_accuracy.setDragEnabled(true);
    }

    /***
     * Function Deals setting patient information variables
     */
    public void setPatientInfo(){
        patientSession = getArguments().getParcelable("patientSession");
        extractPatientData();
    }


    /***
     * Function cleans the data and remove uncesary (-1) values
     */
    private void cleanPatientData() {
        for (ArrayList<Double> list : patientData_all) {
            for (int i = list.size()-1; i>=0; i--) {
                if (list.get(i) == -1) {
                    list.remove(i);
                }
            }
        }
    }


    /***
     * Gets the patient data from the firebase
     */
    private void extractPatientData() {
        patientData_all = patientSession.patientData.biomarkerData.getBiomarkers();
        cleanPatientData();
        accuracy_static_list = patientData_all.get(0);
        accuracy_dynamic_list = patientData_all.get(1);
        speed_static_list = patientData_all.get(2);
        speed_dynamic_list = patientData_all.get(3);
    }

}