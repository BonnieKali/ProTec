import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Session {
    private static final String TAG = "Session";

    // Static singleton Session instance
    private static Session instance;

    // Authentication
    private FirebaseAuth mAuth;

    /**
     * Singleton Session class.
     *
     * Responsible for database connection, synchronization, and user authentication.
     *
     * Private constructor only called once, implementing singleton pattern.
     */
    private Session(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Returns the singleton Session instance. This is a thread-safe call
     *
     * @return Session singleton instance
     */
    public static synchronized Session getInstance(){
        if(instance == null) {
            instance = new Session();
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
        final Boolean[] success = {false};

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success
                            Log.d(TAG, "createUserWithEmailPassword:success");
                            success[0] = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmailPassword:failure", task.getException());
                            Toast.makeText(context, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return success[0];
    }

    /**
     * Signs in user with given email and password.
     *
     * @param context App context to send toast to
     * @param email String user email
     * @param password String user password
     * @return Boolean success
     */
    public Boolean signInUserWithEmailPassword(Context context, String email, String password){
        final Boolean[] success = {false};

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInUserWithEmailPassword:success");
                            success[0] = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInUserWithEmailPassword:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return success[0];
    }



}
