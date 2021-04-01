package com.example.dashboard.recycler;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dashboard.DashboardActivity;
import com.example.dashboard.R;
import com.example.session.user.patient.PatientSession;
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
    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_patient_data, container, false);
        setPatientInfo();                                                                           // Args
        // Chart
        mChart = (LineChart) view.findViewById(R.id.lineChart);
        mChart.setDragEnabled(false);
        mChart.setDragEnabled(true);

        ArrayList<Entry> yVals = new ArrayList<>();

        yVals.add(new Entry(0,60f));
        yVals.add(new Entry(1,60f));
        yVals.add(new Entry(2,60f));
        yVals.add(new Entry(3,60f));
        yVals.add(new Entry(4,60f));
        yVals.add(new Entry(5,60f));
        yVals.add(new Entry(6,60f));

        LineDataSet set1 = new LineDataSet(yVals,"Set1");
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData lineData = new LineData(dataSets);
        mChart.setData(lineData);

        return view;
    }


    /***
     * Function Deals setting patient information variables
     */
    public void setPatientInfo(){
        patientSession = getArguments().getParcelable("patientSession");
    }
}