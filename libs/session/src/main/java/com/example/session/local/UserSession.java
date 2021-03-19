package com.example.session.local;

import android.util.Log;

import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;
import com.google.firebase.auth.FirebaseAuth;


public class UserSession {
    private static final String TAG = "UserSession";

    // Static singleton com.example.session.Session instance
    private static UserSession instance;

    // Authentication
    private FirebaseAuth mAuth;

    /**
     * Singleton com.example.session.Session class.
     *
     * Responsible for database connection, synchronization, and user authentication.
     *
     * Private constructor only called once, implementing singleton pattern.
     */
    private UserSession(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Returns the singleton com.example.session.Session instance. This is a thread-safe call
     *
     * @return com.example.session.Session singleton instance
     */
    public static synchronized UserSession getInstance(){
        if(instance == null) { instance = new UserSession(); }
        return instance;
    }

    /**
     * Creates a new user with a given email and password. It assumes that the both inputs have a
     * correct format.
     *
     * @param email String user email
     * @param password String user password
     * @param callback OnTaskCompleteCallback which runs on the UI thread when operation is finished
     */
    public void createUserWithEmailPassword(String email,
                                            String password,
                                            OnTaskCompleteCallback callback){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Registration successful
                Log.d(TAG, "createUserWithEmailPassword:success");
                callback.onComplete(new TaskResult<>(true));
            } else {
                // Registration failed


                Log.w(TAG, "createUserWithEmailPassword:failure", task.getException());
                callback.onComplete(new TaskResult<>(false));
            }
        });
    }

    /**
     * Signs in user with given email and password. The Result is sent to the UI thread through
     * a callback argument.
     *
     * @param email String user email
     * @param password String user password
     * @param callback OnTaskCompleteCallback which runs on the UI thread when operation is finished
     */
    public void signInUserWithEmailPassword(String email,
                                            String password,
                                            OnTaskCompleteCallback callback){

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in successful
                Log.d(TAG, "signInUserWithEmailPassword:success");
                callback.onComplete(new TaskResult<>());
            } else {
                // Sign in failed
                Log.w(TAG, "signInUserWithEmailPassword:failure", task.getException());
                callback.onComplete(new TaskResult.Error<>(task.getException()));
            }
        });
    }

}
