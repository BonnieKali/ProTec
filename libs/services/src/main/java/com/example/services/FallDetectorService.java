package com.example.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.number.Precision;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.session.Session;
import com.example.session.event.Event;
import com.example.session.event.EventType;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;
import com.google.android.material.slider.Slider;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/***
 * Ilie Galit - s1628465
 * Class implementing the Fall Detection System
 * Responsible for getting sensor values and checking if a fall occured
 * Calculates the SMV and the orientation of the device
 * Notifies Carer in case of fall
 * Works in the backgroudna s a service
 */
public class FallDetectorService implements SensorEventListener {
    // Logging
    private static final String TAG_FALL_DETECTOR = "TAG_FALL_DETECTOR";
    // Class vars
    Context context;
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
    // Thresholds
    private double THRESHOLD_SMV = 80;
    private double THRESHOLD_degreeFloat = 60;
    private double THRESHOLD_degreeFloat2 = 60;
    // Enums
    private enum SETTINGS_KEY {                                         // Key elements from the database
        ACC("ACC"), Q1("Q1"), Q2("Q2");
        private final String key;
        /**
         * @param key
         */
        private SETTINGS_KEY(final String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }
    }
    // Time variables
    private long last_event_time=-1;                                    // time when last seen
    private static final int TIME_EVENT_THRESHOLD=3;                    // Threshold to stay inactive

    /***
     * Constructor for the FallDetector object
     * Assigns args and initializes params
     * @param context: context of activity
     */
    public FallDetectorService(Context context){
        this.context = context;
        checkThresholdValues();                                                                     // Retrieve patient settings from DB
        Session session = Session.getInstance();                                                    // This will be called every time there is a change in the database
        String patientUid = session.getUser().getUID();                                             // patient id
        session.setPatientSettingsListener(patientUid, taskResult -> checkThresholdValues());
        initializeSensors();                                                                        // Initialize sensors
        initializeSensorListeners();                                                                // Listeners
    }


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
     * Populate variables, calculate acceleration and orientation matrices
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!Session.getInstance().isUserSignedIn()){
            sensorManager.unregisterListener(this);
            return;
        }
        // check sensors change
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
        // check if fallen
        checkForFall();
    }


    /***
     * Checks the threshold values in the firebase
     */
    private void checkThresholdValues() {
        Session.getInstance().getPatientSettings(Session.getInstance().getUser().userInfo.id,
                new OnTaskCompleteCallback() {
            @Override
            public void onComplete(TaskResult<?> taskResult) {
                Map<String, Object> settings = (Map<String, Object>) taskResult.getData();
                if (settings!=null) {
                    Log.d(TAG_FALL_DETECTOR, "LOADING PATIENT SETTINGS" + settings.toString());
                    loadPatientSettings(settings);                                                      // extract settings from database
                }
            }
        });
    }

    /***
     * Load values from firebase to current variables
     * @param settings
     */
    private void loadPatientSettings(Map<String, Object> settings) {
        // check if settings exists
        if (isValidSettings(settings)) {                                                            // all thresholds already defined in database
            THRESHOLD_SMV = Double.valueOf(settings.get(SETTINGS_KEY.ACC.getKey()).toString());
            THRESHOLD_degreeFloat = Double.valueOf(settings.get(SETTINGS_KEY.Q1.getKey()).toString());
            THRESHOLD_degreeFloat2 = Double.valueOf(settings.get(SETTINGS_KEY.Q2.getKey()).toString());
        }
    }


    /***
     * Function checks if the keys are present in the database
     * @param settings
     * @return
     */
    private boolean isValidSettings(Map<String, Object> settings) {
        for (SETTINGS_KEY settings_key : SETTINGS_KEY.values()) {
            if (!settings.containsKey(settings_key.getKey())){
                return false;
            }
        }
        return true;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
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
     * Help From: https://codereview.stackexchange.com/questions/118416/matrix-multiplication-in-java
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
     * Function detects of a fall took place whenever new data is available
     *
     */
    public void checkForFall () {
        getOrientationAngles(1.0f - LPF_COF);                             // Get orientation Angles
        double SMV = Math.sqrt(vector_acc[0] * vector_acc[0] + vector_acc[1] *      // Calculate acceleration magnitude
                vector_acc[1] + vector_acc[2] * vector_acc[2]);
        if (SMV > THRESHOLD_SMV) {                                                  // Check passed threshold
            calculateRotationAngles();                                              // get the value in degrees of the orientation
            if (Q1 > THRESHOLD_degreeFloat || Q2 > THRESHOLD_degreeFloat2) {        // Check for threshold
                Log.i(TAG_FALL_DETECTOR,"PERSON FELL");
                if ((last_event_time==-1) || (System.currentTimeMillis()
                        - last_event_time > TIME_EVENT_THRESHOLD*1000)) {           // Notify that fall took place
                    Log.i(TAG_FALL_DETECTOR,"STATEMENT TRUE");
                    last_event_time = System.currentTimeMillis();                   // Update time threshold
                    notifyPatientFallen();                                          // user has fallen procedure
                }
            } else {
                Log.i(TAG_FALL_DETECTOR,"DANGER BUT STANDING");               // No fall took place
            }
        }
        updateVariables();                                                          // Update variables with new values
    }


    /***
     * Update the Gyro Matrix with new values calculated
     */
    private void updateVariables() {
        matrix_gyro = getMatrixGyro(angles_orientation);
        System.arraycopy(angles_orientation, 0, angles_gyro, 0, 3);
    }


    /***
     * Calculates orientation the angles from radians to degrees
     * Makes sure the value is positive
     */
    private void calculateRotationAngles() {
        Q1 = (float) (angles_orientation[1] * 180 / Math.PI);                   // Calculate first angle
        Q2 = (float) (angles_orientation[2] * 180 / Math.PI);                   // Calculate second angle
        // Check angles if actually went over threshold
        if (Q1 < 0)                                                             // Flip to positive
            Q1 = Q1 * -1;
        if (Q2 < 0)                                                             // Flip to positive
            Q2 = Q2 * -1;
    }


    /***
     *  Calculates the angle of the orientation of teh device
     *  Resulted angles will be in radians
     */
    private void getOrientationAngles(float coefficient){
        // Calculate orientation
        angles_orientation[0] = LPF_COF * angles_gyro[0] + coefficient * angles_acc_mag[0];
        angles_orientation[1] = LPF_COF * angles_gyro[1] + coefficient * angles_acc_mag[1];
        angles_orientation[2] = LPF_COF * angles_gyro[2] + coefficient * angles_acc_mag[2];
    }


    /***
     * Procedure followed when patient falls
     * Sets live event in the firebase
     * Sets the User Settings Fallen to True
     */
    private void notifyPatientFallen() {
        Log.d(TAG_FALL_DETECTOR,"SETTING NOTIFICATION");
        Session.getInstance().generateLivePatientNotification(Session.getInstance().getUser().getUID(),"FALLEN","MESSAGE");
        // set live event
        Session.getInstance().generateLiveEvent(EventType.FELL);
        // change user settings
        Session.getInstance().setPatientSetting(Session.getInstance().getUser().userInfo.id,"Fallen",true);
    }
}
