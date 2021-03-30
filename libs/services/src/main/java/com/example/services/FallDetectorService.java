package com.example.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.number.Precision;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.session.Session;
import com.example.session.event.Event;
import com.example.session.event.EventType;
import com.google.android.material.slider.Slider;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

/***
 * Class responsible for getting sensor values and checking if a fall occured
 */
public class FallDetectorService implements SensorEventListener {
    // Logging
    private static final String TAG_FALL_DETECTOR = "TAG_FALL_DETECTOR";
    // Class vars
    Context context;
//    View view;
    // Sensors
    private SensorManager sensorManager;
    private Sensor sensor_acc, sensor_gyr, sensor_mag;
    // Vectors
    private float[] vector_acc = new float[3];          // Acceleration vector
    private float[] vector_gyro = new float[3];         // Gyroscope Vector
    private float[] vector_mag = new float[3];          // Magnitude Vector
    // Quaternions
    private float Q1;                                   // X Degree
    private float Q2;                                   // Y Degree
    // Matrices
    private float[] matrix_gyro = new float[9];         // Gyro Matrix
    private float[] matrix_rotation = new float[9];     // Rotation matrix
    // Angles for orientation
    private float[] angles_gyro = new float[3];         // Angles from gyro
    private float[] angles_acc_mag = new float[3];      // Angles from Acc and Mag
    private float[] angles_orientation = new float[3];  // Angles from fused sensor values
    // Constants
    private static final float LPF_EPS = 0.000000001f;          // Epsilon value for LPF
    private static final float LPF_COF = 0.98f;                 // LPF coefficient
    private static final float NS2S = 1.0f / 1000000000.0f;     // constant to convert nanoseconds to seconds.
    private float timestamp;                                    // time interval between gyro updates
    // States
    private enum STATE{INITIAL, ONGOING};
    private STATE STATE_CURRENT = STATE.INITIAL;
    // UI vars
//    TextView tv_smv, tv_degreeFloat, tv_degreeFloat2, tv_smv_max_old, tv_smv_max_new, tv_nr_falls,
//            tv_smv_fall,  tv_slider_thresh_smv, tv_slider_thresh_degree1, tv_slider_thresh_degree2;
//    Slider slider_thresh_smv, slider_thresh_degree1, slider_thresh_degree2;
    // Testing Vars
    private int count_falls_detected = 0;
    private double SMV_max_new = 0;
    private double SMV_max_old = 0;
    // Thresholds
    private double THRESHOLD_SMV = 10;
    private double THRESHOLD_degreeFloat = 30;
    private double THRESHOLD_degreeFloat2 = 30;


    /***
     * Constructor for the FallDetector object
     * Assigns args and initializes params
     * @param context: context of activity
     */
    public FallDetectorService(Context context){
        this.context = context;
//        this.view = view;
//        initializeUI();
//        initializeSliderListeners();
        initializeSensors();
        initializeSensorListeners();
    }


    /***
     * Initializes all UI element on screen
     */
//    private void initializeUI(){
//        // Usual TextView updates
//        tv_smv = view.findViewById(R.id.tv_smv_value);
//        tv_degreeFloat = view.findViewById(R.id.tv_deg_float_1_value);
//        tv_degreeFloat2 = view.findViewById(R.id.tv_deg_float_2_value);
//        tv_smv_max_new = view.findViewById(R.id.tv_smv_max_new_value);
//        tv_smv_max_old = view.findViewById(R.id.tv_smv_max_old__value);
//        tv_smv_fall = view.findViewById(R.id.tv_nr_falls_detected_smv_value);
//        tv_nr_falls = view.findViewById(R.id.tv_nr_falls_detected_value);
//        // Sliders
//        slider_thresh_smv = view.findViewById(R.id.slider_thresh_smv);
//        slider_thresh_degree1 = view.findViewById(R.id.slider_thresh_degree1);
//        slider_thresh_degree2 = view.findViewById(R.id.slider_thresh_degree2);
//        // TextView related to sliders
//        tv_slider_thresh_smv = view.findViewById(R.id.tv_slider_thresh_smv_value);
//        tv_slider_thresh_degree1 = view.findViewById(R.id.tv_slider_thresh_degree1_value);
//        tv_slider_thresh_degree2 = view.findViewById(R.id.tv_slider_thresh_degree2_value);
//    }


    /***
     * Initializes all sensors used
     */
    private void initializeSensors() {
        sensorManager = (SensorManager)context.getSystemService( Context.SENSOR_SERVICE );  // Sensor Manager
        sensor_acc = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );           // get Accelerometer sensor
        sensor_gyr = sensorManager.getDefaultSensor( Sensor.TYPE_GYROSCOPE );               // get Accelerometer sensor
        sensor_mag = sensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );          // get Accelerometer sensor
    }


    /***
     * Register Listener fot the sensors used
     */
    private void initializeSensorListeners() {
        sensorManager.registerListener( this, sensor_acc, SensorManager.SENSOR_DELAY_GAME );
        sensorManager.registerListener( this, sensor_gyr, SensorManager.SENSOR_DELAY_GAME );
        sensorManager.registerListener( this, sensor_mag, SensorManager.SENSOR_DELAY_GAME );
    }


    /***
     * Register Listeners for the sliders
     */
//    private void initializeSliderListeners(){
//        slider_thresh_smv.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
//                tv_slider_thresh_smv.setText(""+value);
//                THRESHOLD_SMV = value;
//            }
//        });
//        slider_thresh_degree1.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
//                tv_slider_thresh_degree1.setText(""+value);
//                THRESHOLD_degreeFloat = value;
//            }
//        });
//        slider_thresh_degree2.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
//                tv_slider_thresh_degree2.setText(""+value);
//                THRESHOLD_degreeFloat2 = value;
//            }
//        });
//    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(sensorEvent.values, 0, vector_acc, 0, 3);
                setMatrixOrientation();
                break;
            case Sensor.TYPE_GYROSCOPE:
                processGyroData(sensorEvent);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(sensorEvent.values, 0, vector_mag, 0, 3);
                break;
        }
        calculateFusedOrientationTask();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do Nothing
    }


    /***
     * Calculates the Orientation according to the accelerometer and magnetometer values
     */
    public void setMatrixOrientation() {
        // check if rotation matrix is not null
        if (SensorManager.getRotationMatrix(matrix_rotation, null, vector_acc, vector_mag)) {
            // store the orientation
            SensorManager.getOrientation(matrix_rotation, angles_acc_mag);
        }
    }


    /***
     * Sets Rotation Vector given Gyro data
     * output of the gyroscope is integrated over time to calculate a rotation
     * describing the change of angles over the timestep
     * info: http://java.llp.dcc.ufmg.br:8080/apiminer/static/docs/guide/topics/sensors/sensors_motion.html
     * @param gyroValues
     * @param deltaRotationVector
     * @param timeFactor
     */
    private void getRotationVectorFromGyro(float[] gyroValues,
                                           float[] deltaRotationVector,
                                           float timeFactor) {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =
                (float) Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > LPF_EPS) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }


    /***
     * Function Responsible to process the Gyro data
     * Calculates & stores rotation angles from the gyro data
     * info: http://java.llp.dcc.ufmg.br:8080/apiminer/static/docs/guide/topics/sensors/sensors_motion.html
     * @param sensorEvent: new gyro data available
     */
    public void processGyroData(SensorEvent sensorEvent) {
        if (angles_acc_mag == null) {                                           // make sure acc & mag orientation is available
            return;
        }
        if (STATE_CURRENT==STATE.INITIAL) {                                     // set initial gyro matrix if in initial state
            STATE_CURRENT = STATE.ONGOING;
            float[] matrix_gyro_init;
            float[] vec3 = new float[3];
            matrix_gyro_init = getMatrixGyro(angles_acc_mag);
            SensorManager.getOrientation(matrix_gyro_init, vec3);
            matrix_gyro = matrixMultiplication(matrix_gyro, matrix_gyro_init);
        }
        // Convert gyro data to gyro rotation matrix
        float[] deltaRotationVector = new float[4];
        if (timestamp != 0) {
            final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
            System.arraycopy(sensorEvent.values, 0, vector_gyro, 0, 3);
            getRotationVectorFromGyro(vector_gyro, deltaRotationVector, dT / 2.0f);
        }
        timestamp = sensorEvent.timestamp;                                              // save timestamp for the next measurement
        float[] deltaMatrix = new float[9];                                             // convert rotation vector into rotation matrix
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaRotationVector);    // apply the new rotation interval on the gyroscope based rotation matrix
        matrix_gyro = matrixMultiplication(matrix_gyro, deltaMatrix);
        SensorManager.getOrientation(matrix_gyro, angles_gyro);                         // get the gyroscope based orientation from the rotation matrix
    }


    /***
     * Calculate the Rotation Matrix from Orientation
     * To get orientation as a from of euler angles (pitch, roll, azimuth) in Android,
     * it is required to execute followings:
     *   1)SensorManager.getRotationMatrix(float[] R, float[] I, float[] gravity, float[] geomagnetic);
     *   2)SensorManager.getOrientation(float[] R, float[] orientation);
     * info: https://stackoverflow.com/questions/32372847/android-algorithms-for-sensormanager-getrotationmatrix-and-sensormanager-getori
     * @param orientation
     * @return
     */
    private float[] getMatrixGyro(float[] orientation) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(orientation[1]);
        float cosX = (float) Math.cos(orientation[1]);
        float sinY = (float) Math.sin(orientation[2]);
        float cosY = (float) Math.cos(orientation[2]);
        float sinZ = (float) Math.sin(orientation[0]);
        float cosZ = (float) Math.cos(orientation[0]);

        // pitch: X axis rotation
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // roll: Y axis rotation
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // azimuth: z axis rotation
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;
        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }


    /***
     * Function that multiplies two matrices
     * @param A: First Matrix
     * @param B: Second Matrix
     * @return Multiplication Matrix
     */
    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];
        // 0-2
        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
        // 3-5
        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
        // 6-8
        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
        // return
        return result;
    }


    /***
     * Function used to round up to two decimal places
     * @param value: value to round
     * @param places: decimal places to use
     * @return
     */
    public double rounder(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    /***
     * Function detects of a fall took place
     */
    public void calculateFusedOrientationTask () {
//        Log.i(TAG_FALL_DETECTOR,"CHECK FOR FALL");
//        Session.getInstance().generateLiveEvent(EventType.FELL);
        float oneMinusCoeff = 1.0f - LPF_COF;
        // Calculate orientation
        angles_orientation[0] = LPF_COF * angles_gyro[0] + oneMinusCoeff * angles_acc_mag[0];
        angles_orientation[1] = LPF_COF * angles_gyro[1] + oneMinusCoeff * angles_acc_mag[1];
        angles_orientation[2] = LPF_COF * angles_gyro[2] + oneMinusCoeff * angles_acc_mag[2];
        // Calculate acceleration magnitude
        double SMV = Math.sqrt(vector_acc[0] * vector_acc[0] + vector_acc[1] * vector_acc[1] + vector_acc[2] * vector_acc[2]);
        // Update UI
//        tv_smv.setText("" + rounder(SMV, 2));
        if (SMV > SMV_max_new) {
            SMV_max_old = SMV_max_new;
            SMV_max_new = SMV;
//            tv_smv_max_new.setText("" + rounder(SMV_max_new, 2));
//            tv_smv_max_old.setText("" + rounder(SMV_max_old, 2));
        }
        if (SMV > THRESHOLD_SMV) {                                                  // Check passed threshold
            Q1 = (float) (angles_orientation[1] * 180 / Math.PI);                   // Calculate first angle
            Q2 = (float) (angles_orientation[2] * 180 / Math.PI);                   // Calculate second angle
            // Update UI
//            tv_degreeFloat.setText(("" + rounder(Q1, 2)));
//            tv_degreeFloat2.setText(("" + rounder(Q2, 2)));
            // Check angles if actually went over threshold
            if (Q1 < 0)                                                             // Flip to positive
                Q1 = Q1 * -1;
            if (Q2 < 0)                                                             // Flip to positive
                Q2 = Q2 * -1;
            if (Q1 > THRESHOLD_degreeFloat || Q2 > THRESHOLD_degreeFloat2) {        // Check for threshold
                // Update UI
                count_falls_detected += 1;
//                tv_nr_falls.setText("" + count_falls_detected);
//                tv_smv_fall.setText("" + rounder(SMV, 2) + " " + "" + rounder(Q1, 2) + " " + "" + rounder(Q2, 2));
                // Notify that fall took palce
//                Toast.makeText(context, "Sensed Danger! Sending SMS", Toast.LENGTH_SHORT).show();
                Log.i(TAG_FALL_DETECTOR,"PERSON FELL");
                Session.getInstance().generateLiveEvent(EventType.FELL);
            } else {
                // No fall took place
//                Toast.makeText(context, "Sudden Movement! But looks safe", Toast.LENGTH_SHORT).show();
                Log.i(TAG_FALL_DETECTOR,"PERSON STANDING");
//                Session.getInstance().generateLiveEvent(EventType.FELL);
            }
        }
        // Update gyro matrix
        matrix_gyro = getMatrixGyro(angles_orientation);
        System.arraycopy(angles_orientation, 0, angles_gyro, 0, 3);
    }
}
