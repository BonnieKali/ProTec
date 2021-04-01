package com.example.deadreckoning.orientation;

import android.hardware.SensorManager;

public final class MagneticFieldOrientation {

    private MagneticFieldOrientation() {}

    private static float MagneticDeclination = (float) 0.0; //it is later calculated in orientation matrix


    public static float[][] calcOrientationMatrix(float[] gyro_m, float[] magn_m){

        float [] rotMatrix = new float[9];
        float[] inclinationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotMatrix, inclinationMatrix, gyro_m, magn_m);


        //This uses the World Magnetic Model produced by the United States National Geospatial-Intelligence Agency.
        // More details about the model can be found at http://www.ngdc.noaa.gov/geomag/WMM/DoDWMM.shtml.
        // This class currently uses WMM-2020 which is valid until 2025, but should produce acceptable results
        // for several years after that. Future versions of Android may use a newer version of the model.
        MagneticDeclination = SensorManager.getInclination(inclinationMatrix);

        float[][] rotMatrix2D = new float[3][3];
        rotMatrix2D[0][0] = rotMatrix[0];
        rotMatrix2D[0][1] = -rotMatrix[1];
        rotMatrix2D[0][2] = rotMatrix[2];
        rotMatrix2D[1][0] = rotMatrix[3];
        rotMatrix2D[1][1] = -rotMatrix[4];
        rotMatrix2D[1][2] = rotMatrix[5];
        rotMatrix2D[2][0] = -rotMatrix[6];
        rotMatrix2D[2][1] = rotMatrix[7];
        rotMatrix2D[2][2] = -rotMatrix[8];


        return rotMatrix2D;
    }


    public static float getHeading(float[] G_values, float[] M_values) {
        float[][] orientationMatrix = calcOrientationMatrix(G_values,M_values);
        float headingRadians = (float) Math.atan2(orientationMatrix[1][0], orientationMatrix[0][0]);

        //use minus sign for magneticdeclination since it is calculated as negative
        float trueHeadingRadians = (float) (headingRadians - Math.toRadians(MagneticDeclination));
        return trueHeadingRadians;
    }

}
