package com.example.deadreckoning;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.deadreckoning.extra.MathematicalFunctions;
import com.example.deadreckoning.orientation.GyroscopeEulerOrientation;
import com.example.deadreckoning.orientation.MagneticFieldOrientation;
import com.example.deadreckoning.orientation.gyroDeltaOrientation;
import com.example.deadreckoning.stepcounting.stepCounter;
import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.example.session.user.data.deadreckoning.DRData;
import com.example.session.user.data.deadreckoning.DRDataBuilder;
import com.example.session.user.patient.PatientSession;


public class DeadReckoning implements LocationListener, SensorEventListener {
    private static final String TAG = "DeadReckoning";

    private stepCounter improvedStepCounter;

    private gyroDeltaOrientation gyroscopeDeltaOrientation;
    private GyroscopeEulerOrientation gyroscopeEulerOrientation;
    private long startTime;

    private SensorManager sensorManager;

    float[] currGravity; //current gravity
    float[] currMag; //current magnetic field

    private boolean isTracking;
    private boolean isCalibrated;
    private boolean useDefaultStepCounter;
    private float strideLength;
    private float gyroHeading;
    private float magHeading;

    private boolean firstIter;

    private float initialHeading;


    /**
     * Constructor
     */
    public DeadReckoning(Context context){
        // Get location permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },0);
        }

        currGravity = null;
        currMag = null;

        String stepCounterSensitivity;

        isTracking = isCalibrated = useDefaultStepCounter = false;
        firstIter = true;
        strideLength = 0;
        initialHeading = gyroHeading = magHeading = 0;

        // Getting global settings
//        strideLength = getIntent().getFloatExtra("stride_length", 2.3f);
        strideLength = 2.3f;

//        isCalibrated = getIntent().getBooleanExtra("is_calibrated", false);
//         stepCounterSensitivity = getIntent().getStringExtra("step_counter_type");
        stepCounterSensitivity = "1";

        // Edit this
        boolean step_detector = false;

        //usingDefaultCounter uses built in step counter if the step counter sensor is available (false is the default Value)
        //AND if it was set as desired: "default".
        //other wise if step countery type is something different than "default" it will use the dynamic step counter
        //which is much more accurate.
        useDefaultStepCounter = stepCounterSensitivity.equals("default") && step_detector;
        if (useDefaultStepCounter)
            improvedStepCounter = null;
        else if (!stepCounterSensitivity.equals("default"))
            improvedStepCounter = new stepCounter(Double.parseDouble(stepCounterSensitivity));
        else
            improvedStepCounter = new stepCounter(1.15);


        //GYROSCOPE DELTA ORIENTATION OBJECT
        gyroscopeDeltaOrientation = new gyroDeltaOrientation();

        //starting sensors
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        registerListeners();

    }

    public void registerListeners(){
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);

        //************SENSORS **********************
        //in the case we use manual calibration
        //THIS CAN BE USED FOR FUTURE IMPLEMENTATION OF CALIBRATION ACTIVITY
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        if (useDefaultStepCounter) {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                    SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            //we use acceleration data to find peaks and infer step counts. See stepCounter class.
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            currGravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD ||
                event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            currMag = event.values;
        }

        if (currGravity != null && currMag != null){
            // First time the patient is indoors
            Session session = Session.getInstance();

            // Make sure the current user is a patient
            if (session.getUser().userInfo.userType == UserInfo.UserType.CARER){
                Log.w(TAG, "User is carer, we cannot perform DR on him");
                return;
            }

            // Retrieve patientSession object from session
            PatientSession patient = (PatientSession) session.getUser();
            if(patient.patientData.locationData.getleftGeofence() == false){
                if(!isTracking) {
                    Log.d(TAG, "onSensorChanged: User is logged in and Tracking was set to" +
                            " false. We now begin tracking and create a new DRData object");
                    DRData newData = DRDataBuilder.buildDRData();
                    newData.addPoint(0, 0);
                    ((PatientSession) Session.getInstance().getUser()).patientData.drData.add(newData);

                    Log.d(TAG,  ((PatientSession) Session.getInstance().getUser()).patientData.drData.toString());

                    float[][] initialOrientation = MagneticFieldOrientation.calcOrientationMatrix(currGravity, currMag);
                    initialHeading = MagneticFieldOrientation.getHeading(currGravity, currMag);
                    gyroscopeEulerOrientation = new GyroscopeEulerOrientation(initialOrientation);
                    isTracking = true;
                }
            }else{
                Log.d(TAG, "onSensorChanged: User is logged out and isTracking is set to False");
                firstIter = true;
                isTracking = false;
            }
        }

        if (firstIter) {
            startTime = event.timestamp;
            firstIter = false;
        }


        if (isTracking) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.getType() ==
                    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {

                magHeading = MagneticFieldOrientation.getHeading(currGravity, currMag);

            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE ||
                    event.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {

                float[] deltaOrientation = gyroscopeDeltaOrientation.calcDeltaOrientation(event.timestamp, event.values);

                gyroHeading = gyroscopeEulerOrientation.getHeading(deltaOrientation);
                gyroHeading += initialHeading;

            }

            //force of gravity is removed from accel data
            else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                //The advantage of using R (acceleration norm) versus any particular acceleration data direction
                // is that it is impartial to device orientation
                //and can handle dynamic representations of the device.
                float accelMag = MathematicalFunctions.calcMagnitude(
                        event.values[0] +
                                event.values[1] +
                                event.values[2]
                );

                boolean newStep = improvedStepCounter.findStep(accelMag);

                if (newStep) {
                    calculate();
                    Log.d(TAG, "onSensorEvent: New step! "+ improvedStepCounter.getNumSteps());
                }

            } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

                boolean stepFound = (event.values[0] == 1);

                if (stepFound) {
                    calculate();
                }
            }
        }
    }


    public void calculate(){
        Session session = Session.getInstance();

        // Make sure the current user is a patient
        if (session.getUser().userInfo.userType == UserInfo.UserType.CARER){
            Log.w(TAG, "User is carer, we cannot perform DR on him");
            return;
        }

        // Retrieve patientSession object from session
        PatientSession patient = (PatientSession) session.getUser();

        // Retrieve the latest drData object from the patient data list
        int drData_list_size = patient.patientData.drData.size();
        DRData drData = patient.patientData.drData.get(drData_list_size - 1);

        //complimentary filter on mag heading and gyro heading
        float compHeading = GyroscopeEulerOrientation.calcCompHeading(magHeading, gyroHeading);

        //getting and rotating the previous XY points so North 0 on unit circle
        float oPointX = drData.getLastYPoint();
        float oPointY = -drData.getLastXPoint();

        //calculating XY points from heading and stride_length
        //adding to the previous calculated X and Y points
        oPointX += MathematicalFunctions.getXFromPolar(strideLength, compHeading);
        oPointY += MathematicalFunctions.getYFromPolar(strideLength, compHeading);

        //rotating points by 90 degrees, so north is up
        float rPointX = -oPointY;
        float rPointY = oPointX;

        drData.addPoint(rPointX, rPointY);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onLocationChanged(@NonNull Location location) {}


    public void stop(){
        sensorManager.unregisterListener(this);
    }

    public void resume(){
        registerListeners();
    }
}
