package com.example.session;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class UserSession {
    private static final String TAG = "UserSession";

    private static final int AUTH_CHECK_INTERVAL_MS = 500;  // Time interval to check auth result
    private static final int AUTH_TIMEOUT_S = 5;            // Timeout for authentication

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
        if(instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Creates a new user with a given email and password. It assumes that the both inputs have a
     * correct format.
     *
     * @param context App context to send toast to
     * @param email String user email
     * @param password String user password
     * @return Boolean success
     */
    public Boolean createUserWithEmailPassword(Context context, String email, String password){
        final Boolean[] result = {false};

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration result
                            Log.d(TAG, "createUserWithEmailPassword:result");
                            result[0] = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmailPassword:failure", task.getException());
                            Toast.makeText(context, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return result[0];
    }

    /**
     * Signs in user with given email and password. This is a blocking statement so it should be
     * used in a background thread.
     *
     * @param context App context to send toast to
     * @param email String user email
     * @param password String user password
     * @return Boolean success
     */
    public Boolean signInUserWithEmailPassword(Context context, String email, String password){
        Boolean[] result = {null};

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in result, update UI with the signed-in user's information
                            Log.d(TAG, "signInUserWithEmailPassword:success");
                            result[0] = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInUserWithEmailPassword:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            result[0] = false;
                        }
                    }
                });

        return waitForAuthResult(result);
    }

    /**
     * Blocking statement which accepts an initialized Boolean result array with a null value. It
     * waits until the array gets updated from another thread and then exits the blocking while
     * loop. If the AUTH_TIMEOUT_S is exceeded, then it sets the first element of the array to false
     * and returns.
     *
     * @param result Boolean[] array with one element
     * @return Boolean result of authentication
     */
    private Boolean waitForAuthResult(Boolean[] result){
        int time_lapsed_ms = 0;
        int auth_timeout_ms = AUTH_TIMEOUT_S * 1000;

        while(result[0] == null){
            SystemClock.sleep(AUTH_CHECK_INTERVAL_MS);
            time_lapsed_ms += AUTH_CHECK_INTERVAL_MS;

            if(time_lapsed_ms > auth_timeout_ms){
                result[0] = false;
            }
        }

        return result[0];
    }



}
