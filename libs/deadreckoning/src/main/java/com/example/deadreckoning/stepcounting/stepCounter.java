package com.example.deadreckoning.stepcounting;
/**
 * Jose Ignacio (nacho)
 * s1616915
 * Dynamic step counting class. Deals with acceleration data to find peaks and steps
 */
public class stepCounter {

    private int stepCount;
    private double sensitivity;
    private double upperThreshold, lowerThreshold;

    private boolean firstIter;
    private boolean isPeak;


    private double averageAccMag;
    private int iterator;

    public stepCounter() {

        stepCount = 0;
        sensitivity = 1.0;
        upperThreshold = 0;
        lowerThreshold = 0;

        firstIter = true;
        isPeak = false;



        averageAccMag = 0;
        iterator = 0;

    }

    //Set the default values for variables
    public stepCounter(double sensitivity) {
        this();
        this.sensitivity = sensitivity;
    }


    public boolean findStep(double accMagnitude) {

        setPeakDetectionThresholds(accMagnitude);

        //If mag is above threshold then it is a peak
        if (accMagnitude > upperThreshold) {
            if (!isPeak) {
                stepCount++;
                isPeak = true;
                return true;
            }
        }


        else if (accMagnitude < lowerThreshold) {
            if (isPeak) {
                isPeak = false;
            }
        }

        return false;
    }

    public void setPeakDetectionThresholds(double acc) {

        iterator++;

        if (firstIter) {
            upperThreshold = acc + sensitivity;
            lowerThreshold = acc - sensitivity;

            averageAccMag = acc;

            firstIter = false;
            return;
        }

        //Performing a moving average here
        averageAccMag = ((averageAccMag) * (((double) iterator -1.0)/(double) iterator)) + (acc/(double) iterator);

        upperThreshold = averageAccMag + sensitivity;
        lowerThreshold = averageAccMag - sensitivity;

    }

    public double getSensitivity() {
        return sensitivity;
    }

    public int getNumSteps() {
        return stepCount;
    }

    public void clearStepCount() {
        stepCount = 0;
    }

}
