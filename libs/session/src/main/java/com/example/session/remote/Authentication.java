package com.example.session.remote;

import android.util.Log;

import com.example.session.user.UserSessionBuilder;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class Authentication {
    private static final String TAG = "Authentication";

    // Authentication
    private FirebaseAuth mAuth;


    /**
     * Class that deals with remote registration and authentication.
     */
    public Authentication(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Creates a new user with a given email and password.
     *
     * @param email String user email
     * @param password String user password
     * @return TaskResult(String) containing the uid of the newly created user. If there is an error
     * then the result will be TaskResult.Error containing the exception that caused it.
     */
    public TaskResult<String> createUserWithEmailPassword(String email, String password){
        TaskResult<String> result;

        // Authenticate the user in a synchronous statement
        try {
            Task<AuthResult> task = mAuth.createUserWithEmailAndPassword(email, password);
            AuthResult authResult = Tasks.await(task);  // Here we wait for the result
            if (task.isSuccessful()) {
                // Registration successful
                Log.d(TAG, "createUserWithEmailPassword: Success");
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null){
                    String uid = user.getUid();
                    result = new TaskResult<>(uid);
                } else {
                    Log.w(TAG, "createUserWithEmailPassword: Current user was null");
                    result = new TaskResult.Error<>(new Exception("Firebase User was null"));
                }
            } else {
                // Sign in failed
                Log.w(TAG, "createUserWithEmailPassword: Failure", task.getException());
                result = new TaskResult.Error<>(task.getException());
            }
        } catch (ExecutionException e) {
            Log.w(TAG, "createUserWithEmailPassword: Failure", e);
            result = new TaskResult.Error<>(e);
        } catch (InterruptedException e) {
            Log.w(TAG, "createUserWithEmailPassword: Failure", e);
            result = new TaskResult.Error<>(e);
        }

        return result;
    }

    /**
     * Signs in user with given email and password. This is a blocking statement, so it should be
     * called in a background thread.
     *
     * @param email String user email
     * @param password String user password
     * @return TaskResult(String) containing the uid of the signed in user. If there is an error
     *          then the result will be TaskResult.Error containing the exception caused
     */
    public TaskResult<String> signInUserWithEmailPassword(String email, String password){
        TaskResult<String> result;

        // Authenticate the user in a synchronous statement
        try {
            Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
            AuthResult authResult = Tasks.await(task);  // Here we wait for the result
            if (task.isSuccessful()) {
                // Sign in successful
                Log.d(TAG, "signInUserWithEmailPassword: Success");
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null){
                    String uid = user.getUid();
                    result = new TaskResult<>(uid);
                } else {
                    Log.w(TAG, "signInUserWithEmailPassword: Current user was null");
                    result = new TaskResult.Error<>(new Exception("Firebase User was null"));
                }
            } else {
                // Sign in failed
                Log.w(TAG, "signInUserWithEmailPassword: Failure", task.getException());
                result = new TaskResult.Error<>(task.getException());
            }
        } catch (ExecutionException e) {
            Log.w(TAG, "signInUserWithEmailPassword: Failure", e);
            result = new TaskResult.Error<>(e);
        } catch (InterruptedException e) {
            Log.w(TAG, "signInUserWithEmailPassword: Failure", e);
            result = new TaskResult.Error<>(e);
        }

        return result;
    }


    /**
     * Signs out currently authenticated user.
     */
    public void logOut(){
        mAuth.signOut();
    }



}
