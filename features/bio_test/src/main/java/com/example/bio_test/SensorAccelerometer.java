package com.example.bio_test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class SensorAccelerometer implements SensorEventListener {
    // Logging
    private static final String TAG_SENSOR_ACC = "TAG_SENSOR_ACC";
    // Class dependent Vars
    Context context;
    // Sensor Vars
    private SensorManager sensorManager;
    // Sensor Vars
    float acc_x, acc_y, acc_z;

    public SensorAccelerometer(Context context){
        this.context = context;
        SensorManager manager = (SensorManager)context.getSystemService( Context.SENSOR_SERVICE );
        Sensor sensor_acc = manager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        manager.registerListener( this, sensor_acc, SensorManager.SENSOR_DELAY_GAME );
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){          // check sensor type
            acc_x=event.values[0];                                      // assign x direction
            acc_y=event.values[1];                                      // assign y direction
            acc_z=event.values[2];                                      // assign z direction
            // -- DEBUG --
            Log.i(TAG_SENSOR_ACC,"x="+acc_x+"; y="+acc_y+"; z="+acc_z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
