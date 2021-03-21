package com.example.session.user.carer;

import com.example.session.user.UserData;

import java.util.ArrayList;
import java.util.List;

public class CarerData extends UserData {

    public List<String> patients;

    public CarerData(){
        super();
        this.patients = new ArrayList<>();
    }

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
