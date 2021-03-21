package com.example.session.user.carer;

import com.example.session.user.UserData;

import java.util.ArrayList;
import java.util.List;

public class CarerData extends UserData {

    // List containing all patient ids assigned to this carer
    public List<String> patients;

    /**
     * Default Empty Constructor
     */
    public CarerData(){
        super();
        this.patients = new ArrayList<>();
    }

    /**
     * Class holding all carer-specific data
     *
     * @param patients
     */
    public CarerData(List<String> patients){
        super();
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "CarerData{" +
                "patients=" + patients +
                '}';
    }

}
