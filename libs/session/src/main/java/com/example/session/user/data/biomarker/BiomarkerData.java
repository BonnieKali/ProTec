package com.example.session.user.data.biomarker;

public class BiomarkerData {

    private int buttonPressCounter;

    /*
        Customize this class
     */

    public BiomarkerData(int buttonPressCounter) {
        this.buttonPressCounter = buttonPressCounter;
    }


    public void updateCounter(int new_counter) {
        buttonPressCounter = new_counter;
    }

    public int get_buttonPressCounter(){
        return buttonPressCounter;
    }

}
