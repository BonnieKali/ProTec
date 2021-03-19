package com.example.session.data;

import java.util.List;

public class User {

    public String id;
    public String name;
    public List<Integer> locations;

    public User(String id, String name, List<Integer> locations){
        this.id = id;
        this.name = name;
        this.locations = locations;
    }

//    public toMap(){
//        Map<>
//    }

}
