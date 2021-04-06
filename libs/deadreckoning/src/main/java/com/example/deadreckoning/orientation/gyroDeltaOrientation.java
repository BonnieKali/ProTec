package com.example.deadreckoning.orientation;

/**
 * Jose Ignacio (nacho)
 * s1616915
 * This deals with gyro delta orientation per time step for DPR
 */


import com.example.deadreckoning.extra.MathematicalFunctions;

public class gyroDeltaOrientation {

    private boolean firstIter;
    private float lastTimestamp;



    public gyroDeltaOrientation() {
        this.firstIter = true;
    }



    public float[] calcDeltaOrientation(long timestamp, float[] rawGyroValues) {
        //get the first timestamp
        if (firstIter) {
            firstIter = false;
            lastTimestamp = MathematicalFunctions.nsToSec(timestamp);
            return new float[3];
        }

        //return delta Orientation
        return integrateValues(timestamp, rawGyroValues);
    }


    private float[] integrateValues(long timestamp, float[] gyroValues) {
        double timeNow = MathematicalFunctions.nsToSec(timestamp);
        double deltaTime = timeNow - lastTimestamp;

        float[] deltaOrientation = new float[3];

        //gyroValues*dt, summation is integration
        for (int i = 0; i < 3; i++)
            deltaOrientation[i] = gyroValues[i] * (float)deltaTime;

        lastTimestamp = (float) timeNow;

        return deltaOrientation;
    }

}
