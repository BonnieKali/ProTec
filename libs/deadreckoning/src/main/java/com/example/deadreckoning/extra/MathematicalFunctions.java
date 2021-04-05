package com.example.deadreckoning.extra;
/**
 * Jose Ignacio (nacho)
 * s1616915
 * Some mathematical functions to help out during Dead Pedestrian Reckoning calculations
 */

public final class MathematicalFunctions {

    private MathematicalFunctions() {}



    public static final float[][] IDENTITY_MATRIX = {{1,0,0},
                                                     {0,1,0},
                                                     {0,0,1}};

    //calculate x coordinate given radius and angle
    public static float getXFromPolar(double radius, double angle) {
        return (float)(radius * Math.cos(angle));
    }

    //calculate y coordinate given radius and angle
    public static float getYFromPolar(double radius, double angle) {
        return (float)(radius * Math.sin(angle));
    }

    public static float nsToSec(float time) {
        return time / 1000000000.0f;
    }

    //used in the cosine direction calculation
    //this function calculates the fuctorial of number num
    public static int factorial(int num) {
        int factorial = 1;
        for (int i = 1; i <= num; i++) {
            factorial *= i;
        }
        return factorial;
    }

    public static float[][] multiplyMatrices(float[][] a, float[][] b) {

        //numRows = aRows
        int numRows = a.length;

        //numCols = bCols
        int numCols = b[0].length;

        //numElements = (aCols == bRows)
        int numElements = b.length;

        float[][] c = new float[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                for (int element = 0; element < numElements; element++)
                    c[row][col] += a[row][element] * b[element][col];
            }
        }

        //a[][] * b[][] = c[][]
        return c;

    }



    public static float[][] addMatrices(float[][] a, float[][] b) {

        int numRows = a.length;
        int numColumns = a[0].length;

        float[][] c = new float[numRows][numColumns];

        for (int row = 0; row < numRows; row++)
            for (int column = 0; column < numColumns; column++)
                c[row][column] = a[row][column] + b[row][column];

        //a[][] + b[][] = c[][]
        return c;
    }

    //multiplying  a matrix by a scalar
    public static float[][] scaleMatrix(float a[][], float scalar) {

        int numRows = a.length;
        int numColumns = a[0].length;

        float[][] b = new float[numRows][numColumns];

        for (int row = 0; row < numRows; row++)
            for (int column = 0; column < numColumns; column++)
                b[row][column] = a[row][column] * scalar;

        //a[][] * c = b[][]
        return b;

    }

    public static float calcMagnitude(double... args) {
        double sumSq = 0;
        for (double arg : args)
            sumSq += Math.pow(arg, 2);
        return (float) Math.sqrt(sumSq);
    }


}
