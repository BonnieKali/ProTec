package com.example.dashboard.patient;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dashboard.R;
import com.example.session.user.data.deadreckoning.DRData;
import com.example.session.user.patient.PatientSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * create an instance of this fragment.
 */
public class PatientTrackingDayFragment extends Fragment {
        View view;
        PatientSession patientSession;

        boolean isTracking;
        LinearLayout mLinearLayout;
        FloatingActionButton fabButton;
        ScatterPlot scatterPlot; //todo: change location of ScatterPlot class to appropiate place.
        private int leftAtLine = 0;

        private DRData patientDayIndoorData;
        private List<Double> xList;
        private List<Double> yList;

       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_patient_tracking_day, container, false);
       initUI();
       setPatientInfo();
       setOnClickListeners(view);
       return view;
    }




    /***
     * Initialzie UI
     */
    private void initUI() {
        //tv_day = view.findViewById(R.id.tv_day_id);
        mLinearLayout = view.findViewById(R.id.linearLayoutGraph);
        fabButton = view.findViewById(R.id.fab);
        isTracking = false;
        scatterPlot = new ScatterPlot("Patient Tracking");


    }

    /***
     * Button Listeners
     * @param view
     */
    private void setOnClickListeners(View view) {
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftAtLine==xList.size()){
                    isTracking =false;
                    leftAtLine = 0;
                    mLinearLayout.removeAllViews();
                    scatterPlot.clearSet();
                    mLinearLayout.addView(scatterPlot.getGraphView(getActivity().getApplicationContext()));

                }


                if (!isTracking) {
                    isTracking = true;


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            readData();

                        }
                    }).start();

                    fabButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause_black_24dp));

                } else {
                    isTracking = false;
                    fabButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play_arrow_black_24dp));

                }


            }
        });
    }


    /**
     * Reads the X an Y data from patients selected indoor data day
     */
    private void readData(){

        try {


            for(int i=leftAtLine; i<xList.size();i++){
             //START READING X and Y


                Double[] coordinate = new Double[2];
                coordinate[0] = xList.get(i);
                coordinate[1] = yList.get(i);
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        stepPlotter(coordinate);
                        leftAtLine++;

                    }
                });

                //DELAYING VIEW
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // manage error ...
                }



            }



            fabButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play_arrow_black_24dp));
        }catch (Exception e){
            int s = 1;

        }
    }

    /**
     * Plots the X and Y coordinates to graph
     * @param coordinate
     */
    private void stepPlotter(Double[] coordinate){


        float rPointX = (float) -(coordinate[0]);
        float rPointY =  (coordinate[1].floatValue());

        scatterPlot.addPoint(rPointX, rPointY);
        mLinearLayout.removeAllViews();
        mLinearLayout.addView(scatterPlot.getGraphView(getActivity().getApplicationContext()));

    }


    /***
     * Function Deals setting patient information variables
     */
    public void setPatientInfo(){
        patientSession = getArguments().getParcelable("patientSession");
        // get other stuff from patietn session here
        String day_id = getArguments().getString("day_id");
        int day = Integer.parseInt(day_id);

        //TODO: this gets me the latest data. need to change this to any day, but it depends on the Patient Tracking Fragment which needs a way of creating a list
        day = patientSession.patientData.drData.size()-1;
        patientDayIndoorData = patientSession.patientData.drData.get(day);
        xList = patientDayIndoorData.getXList();
        yList = patientDayIndoorData.getYList();

        //tv_day.setText(day_id);
        // extract data after
    }

    /***
     * EXAMPLE OF EXTRACITNG DATA
     */
    private void extractPatientData() {
        ArrayList<ArrayList<Double>> patientData_all = patientSession.patientData.biomarkerData.getBiomarkers();
    }

}