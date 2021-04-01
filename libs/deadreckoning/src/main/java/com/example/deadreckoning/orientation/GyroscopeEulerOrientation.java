
package com.example.deadreckoning.orientation;

import com.example.deadreckoning.extra.MathematicalFunctions;

public class GyroscopeEulerOrientation {

    private float[][] C;

    ///************CONSTRUCTORS***************
    public GyroscopeEulerOrientation() {
        C = MathematicalFunctions.IDENTITY_MATRIX.clone();
    }

    public GyroscopeEulerOrientation(float[][] initialOrientation) {
        this();
        C = initialOrientation.clone();
    }
    //******************************

    public float[][] getOrientationMatrix(float[] gyroValues) {
        float wX = gyroValues[0];
        float wY = gyroValues[1];
        float wZ = gyroValues[2];

//        float wX = gyroValues[1];
//        float wY = gyroValues[0];
//        float wZ = -gyroValues[2];

        float[][] A = calcMatrixA(wX, wY, wZ);

        calcCosineMatrix(A);

        return C.clone();
    }

    public float getHeading(float[] gyroValue) {
        getOrientationMatrix(gyroValue);
        return (float) (Math.atan2(C[1][0], C[0][0]));
    }

    private float[][] calcMatrixA(float wX, float wY, float wZ) {

        float[][] A; //will hold resulting matrix


        float[][] B = calcMatrixB(wX, wY, wZ);
        //skew symmetric matrix
        //This matrix is called the cross product skew symmetric matrix because
        //it can be used to express the cross product of two vectors as a matrix multiplication
        //This matrix is useful for us because it can use used to express the effect of a small rotation.
        float[][] B_sq = MathematicalFunctions.multiplyMatrices(B, B);

        float mag = MathematicalFunctions.calcMagnitude(wX, wY, wZ);
        float B_coeff = B_coeff(mag);
        float B_squared_coeff = B_squared_coeff(mag);

        B = MathematicalFunctions.scaleMatrix(B, B_coeff);
        B_sq = MathematicalFunctions.scaleMatrix(B_sq, B_squared_coeff);

        //does the summation of I + ((sin σ) / σ)B + ((1 - cos σ) / σ^2)B^2
        A = MathematicalFunctions.addMatrices(B, B_sq);
        A = MathematicalFunctions.addMatrices(A, MathematicalFunctions.IDENTITY_MATRIX);

        return A;
    }

    private float[][] calcMatrixB(float wX, float wY, float wZ) {
        //making use of Use of Earth's magnetic field for
        // mitigating gyroscope errors regardless of magnetic perturbation
        //sources:  doi: 10.3390/s111211390
        //doi: 10.3390/s17040832
        return (new float[][]{{0, -wZ, wY},
                              {wZ, 0, -wX},
                              {-wY, wX, 0}});
//          return (new float[][]{{0, wZ, -wY},
//                                {-wZ, 0, wX},
//                                {wY, -wX, 0}});
    }

    //https://www.cl.cam.ac.uk/techreports/UCAM-CL-TR-696.pdf
    //Page 23 shows formula of taylor expansion for tracking oreitnation.
    //C(t+delta) = C(t)[I + ((sin σ) / σ)B + ((1 - cos σ) / σ^2)B^2]
    //which is the attitude update equation used to update C as each new sample becomes available.
    private float B_coeff(float sigma) {
        float sigmaSqOverThreeFactorial = (float) Math.pow(sigma, 2) / MathematicalFunctions.factorial(3);
        float sigmaToForthOverFiveFactorial = (float) Math.pow(sigma, 4) / MathematicalFunctions.factorial(5);
        return (float) (1.0 - sigmaSqOverThreeFactorial + sigmaToForthOverFiveFactorial);
    }

    private float B_squared_coeff(float sigma) {
        float sigmaSqOverFourFactorial = (float) Math.pow(sigma, 2) / MathematicalFunctions.factorial(4);
        float sigmaToForthOverSixFactorial = (float) Math.pow(sigma, 4) / MathematicalFunctions.factorial(6);
        return (float) (0.5 - sigmaSqOverFourFactorial + sigmaToForthOverSixFactorial);
    }

    //Direction Cosine matrix
    private void calcCosineMatrix(float[][] A) {
        C = MathematicalFunctions.multiplyMatrices(C, A);
    }


    public static float calcCompHeading(double magHeading, double gyroHeading) {
        //complimentary filter
//        complementary filters explicitly use the fact that both the gyroscope and the
//        combination of the accelerometer and the magnetometer provide information
//        about the orientation of the sensor

        //CONVERTING FOR COMPLEMENTARY FILTER
        //convert -pi/2 < h < pi/2 to 0 < h < 2pi
        if (magHeading < 0)
            magHeading = magHeading % (2.0 * Math.PI);
        if (gyroHeading < 0)
            gyroHeading = gyroHeading % (2.0 * Math.PI);

        //a*magHeading + (1-a)GyroHeading
        double compHeading = 0.02 * magHeading + 0.98 * gyroHeading;

        //CONVERTING BACK TO PREVIOUS
        //convert 0 < h < 2pi to -pi/2 < h < pi/2
        if (compHeading > Math.PI)
            compHeading = (compHeading % Math.PI) + -Math.PI;

        return (float)compHeading;

    }

}
