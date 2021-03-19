package com.example.session.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static final String DB_URL =
            "https://protec-546cd-default-rtdb.europe-west1.firebasedatabase.app/";

    private static FirebaseDatabase database;
    private static DatabaseReference dRef;

//    public Database(){
//        database = FirebaseDatabase.getInstance();
//    }

    public static void init(){
        database = FirebaseDatabase.getInstance(DB_URL);
        dRef = database.getReference();
    }


    public static void test(){
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Hello, World!");

        DatabaseReference usersRef = dRef.child("users");

        // User
        String id = "de4";
        String name = "alan";
        List<Integer> locations = Arrays.asList(22, 23, 24);

        DatabaseReference alan = usersRef.child(id);

        alan.child("name").setValue(name);

        int i = 0;
        for(Integer loc : locations){
            DatabaseReference alan_locs = alan.child("locations").child(Integer.toString(i));
            i+=1;
            alan_locs.setValue(loc);
        }

    }





}
