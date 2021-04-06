package com.example.bio_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.actions.Actions;
import com.example.session.Session;
import com.example.session.event.Event;
import com.example.session.event.EventType;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableProcess;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;

import java.util.Random;

/**
 * create an instance of this fragment.
 */
public class BioTestButtonFragment extends  Fragment {
    // Logging
    private static final String TAG_LOG_BIO = "TAG_LOG_BIO";
    // Session
    private Session session;                            // global singleton session object
    private PatientSession patientSession;              // session object for patient
    private UserSession userSession;                    // session object for user
    // UI
    private TextView tv_push_counter;                   // text view for number of pushes
    private TextView tv_timer;                          // text view for countdown timer
    private TextView tv_screen_counter;                 // text view for displaying screen presses
    private TextView tv_accuracy;                       // text view for the accuracy
    private TextView tv_speed;
    private ConstraintLayout constraintLayout;          // fragment layout reference
    private Button btn_press;                           // button that is pressed
    private Button btn_restart;                         // button for restarting test
    // Objects
    private CountDownTimer countDownTimer;              // object used to count down time left
    private Random rand;                                // used to generate random values
    private View view;                                  // global screen view
    // Vars
    private int push_counter;                           // counts made per experiment
    private int screen_counter;                         // counts screen presses during tests
    private float final_accuracy;                       // final accuracy passed to database
    private float final_speed;                          // final speed passed to database
    private boolean FLAG_TEST_PERFORMED;                // flag: true when test is performed
    private enum PRESS_TYPE{BUTTON, SCREEN}             // type of screen presses/pushes
    private int time_to_count = 5000;                   // time to count in milliseconds
    private float time_passed;
    private enum TEST_TYPE{STATIC, DYNAMIC, FINISHED}   // types of test that cen be performed
    private TEST_TYPE CURRENT_TEST_TYPE;                // current type of the test performed
    private TEST_TYPE PAST_TEST_TYPE;                   // test that just was performed


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bio_test_button, container, false);
        // get the session & assign to patient session
        patientSession = getSession();
        // Variables
        countDownTimer = getCountDownTimer();
        FLAG_TEST_PERFORMED = false;
        rand = new Random();
        // UI elements
        initializeUI(view);
        // Listeners
        setOnClickListeners(view);
        // Arguments
        CURRENT_TEST_TYPE = getTestType();
        return view;
    }


    /***
     * Gets the current test type performed by the user
     * @return
     */
    private TEST_TYPE getTestType() {
        String arg = getArguments().getString("ARG1");
        if (arg.equals("STATIC")){
            return TEST_TYPE.STATIC;
        } else {
            return TEST_TYPE.DYNAMIC;
        }
    }


    /***
     * Initializes all UI elements
     */
    private void initializeUI(View view) {
        tv_push_counter = view.findViewById(R.id.textView_button_counter_value);
        tv_screen_counter = view.findViewById(R.id.textView_screen_counter_value);
        tv_accuracy = view.findViewById(R.id.textView_accuracy_value);
        tv_speed = view.findViewById(R.id.textView_speed_value);
        tv_timer = view.findViewById(R.id.textView_timer_value);
        tv_timer.setText(time_to_count/1000+"s");
        constraintLayout = view.findViewById(R.id.screen_layout);
        btn_press = view.findViewById(R.id.button_bioCounter);
        btn_restart = view.findViewById(R.id.button_bio_restart_test);
        btn_restart.setVisibility(View.INVISIBLE);                                 // invisible at the start
    }


    /***
     * Returns the appropriate patient session to extract and populate user data
     * @return Patient Session object
     */
    private PatientSession getSession(){
        session = Session.getInstance();
        userSession =  session.getUser();
        if(userSession.userInfo.userType == UserInfo.UserType.PATIENT) {
            return  (PatientSession) userSession;
        } else {
            throw new IllegalStateException("Must be a Patient Session when doing bio markers");
        }
    }


    /***
     * Creates a custom count down timer object
     * @return CountDownTimer object
     */
    private CountDownTimer getCountDownTimer() {
        return new CountDownTimer(time_to_count,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_timer.setText(millisUntilFinished / 1000 + "s");
            }
            @Override
            public void onFinish() {
                tv_timer.setText("Finished");
                PAST_TEST_TYPE  = CURRENT_TEST_TYPE;                                // update last test
                CURRENT_TEST_TYPE = TEST_TYPE.FINISHED;                             // finished state
                final_speed = (float)screen_counter/(float)(time_to_count/1000);    // speed
                tv_speed.setText(String.format("%.2f", final_speed));               // set ui text
                saveUserDataButtonCounter();                                        // Save Data to Firebase
                FLAG_TEST_PERFORMED = false;
                btn_restart.setVisibility(view.VISIBLE);                            // set button to visible
            }
        };
    }


    /***
     * Saves the data from the test performed to the session
     */
    private void saveUserDataButtonCounter(){
        double accuracy_static=-1.,accuracy_dynamic=-1.,speed_static=-1.,speed_dynamic=-1.;
        if(PAST_TEST_TYPE == TEST_TYPE.DYNAMIC){
            accuracy_static  = -1.;
            accuracy_dynamic = final_accuracy;
            speed_static     = -1.;
            speed_dynamic    = final_speed;
        } else {
            accuracy_static  = final_accuracy;
            accuracy_dynamic = -1.;
            speed_static     = final_speed;
            speed_dynamic    = -1.;
        }
        patientSession.patientData.biomarkerData.updateBiomarker(accuracy_static, accuracy_dynamic, speed_static, speed_dynamic);
        session.syncToRemote();
    }


    /**
     * custom on click listener
     * @param view view of the inflated view (all elements on screen)
     */
    private void setOnClickListeners(View view) {
        setScreenLayoutListener(view.findViewById(R.id.screen_layout));             // screen listener
        setBioTestButtonListener(view.findViewById(R.id.button_bioCounter));        // Bio Button
        setResetButtonListener(view.findViewById(R.id.button_bio_restart_test));    // Reset Button
    }


    /***
     * Listener for the reset Test Button
     * Would allow to restart and take a new test
     * @param button
     */
    private void setResetButtonListener(Button button) {
        button.setOnClickListener(v -> {
            if (CURRENT_TEST_TYPE == TEST_TYPE.FINISHED) {
                CURRENT_TEST_TYPE = PAST_TEST_TYPE;
                btn_restart.setVisibility(view.INVISIBLE);
            }
        });
    }


    /***
     * Listener fot the screen layout
     * Used for determining accuracy
     * @param constraintLayout
     */
    private void setScreenLayoutListener(ConstraintLayout constraintLayout) {
        constraintLayout.setOnClickListener(v -> {
            updateOnClick(PRESS_TYPE.SCREEN);
        });
    }


    /**
     * Listener for the BioTest Button
     * @param button_toBioTest: Button
     */
    private void setBioTestButtonListener(Button button_toBioTest) {
        button_toBioTest.setOnClickListener(v -> {
            updateOnClick(PRESS_TYPE.BUTTON);
        });
    }


    /***
     * Update Logic performed on click.
     * Update Counter and count clicks
     */
    private void updateOnClick(PRESS_TYPE type) {
        if (CURRENT_TEST_TYPE != TEST_TYPE.FINISHED) {                                      // already done test
            if (type == PRESS_TYPE.BUTTON) {
                if (FLAG_TEST_PERFORMED == false)
                    startCountingDown();                                                    // if first push
                push_counter += 1;                                                          // count pushes
                screen_counter += 1;                                                        // screen pressed
                final_accuracy = ((float) push_counter/(float) screen_counter) * 100;       // accuracy
                tv_push_counter.setText(String.valueOf(push_counter));                      // update UI
                tv_screen_counter.setText(String.valueOf(screen_counter));                  // Update UI
                tv_accuracy.setText(String.format("%.2f", final_accuracy));
                if (CURRENT_TEST_TYPE == TEST_TYPE.DYNAMIC) {                               // dynamic button
                    setRandomButtonPosition();
                }
            } else if (type == PRESS_TYPE.SCREEN && FLAG_TEST_PERFORMED == true) {
                screen_counter += 1;
                final_accuracy = ((float) push_counter / (float) screen_counter) * 100;     // accuracy
                tv_screen_counter.setText(String.valueOf(screen_counter));
                tv_accuracy.setText(String.format("%.2f", final_accuracy));
            }
        }
    }


    /***
     * Timer used for counting down when
     */
    private void startCountingDown(){
        FLAG_TEST_PERFORMED = true;
        push_counter = 0;
        screen_counter = 0;
        countDownTimer.start();
    }


    /**
     * Called when the activity has detected the user's press of the back
     * key. The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    public void onBackPressed() {
        countDownTimer.cancel();
    }


    /***
     * Change the position of the button to a new random location
     * New position will be with the bounds of the view
     */
    private void setRandomButtonPosition() {
        // screen params
        final int width_view  = view.getWidth();
        final int height_view = view.getHeight();
        //usable screen
        final int width_view_usable_left    = width_view/2;
        final int width_view_usable_right   = width_view/2;
        final int height_view_usable_bottom = height_view/3;
        final int height_view_usable_top    = height_view/3;
        // box size range
        final int minBtnSize = 230;
        final int maxBtnSize = 350;
        // random values
        final int size   = (int) (minBtnSize + (maxBtnSize - minBtnSize) * rand.nextDouble());
        final int left   = (int) (rand.nextFloat() * (width_view_usable_left - size/2));
        final int right  = (int) (rand.nextFloat() * (width_view_usable_right + size/2));
        final int top    = (int) (rand.nextFloat() * (height_view_usable_top - size/2));
        final int bottom = (int) (rand.nextFloat() * (height_view_usable_bottom + size/2));
        // layout params
        final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) btn_press.getLayoutParams();
        // log
        Log.i(TAG_LOG_BIO,"width="+width_view+"; height="+height_view+"; size="+size+"; top="+top+"; left="+left+"; right="+right+"; bottom="+bottom);
        // set new params
        layoutParams.width = size;
        layoutParams.height = size;
        // pick a random margin to set
        if (rand.nextFloat()>0.5) {
            layoutParams.leftMargin = left;
            Log.i(TAG_LOG_BIO, "left="+left);
        } else{
            layoutParams.rightMargin = right;
            Log.i(TAG_LOG_BIO, "right="+right);
        }
        if (rand.nextFloat()>0.5){
            layoutParams.topMargin = top;
            Log.i(TAG_LOG_BIO, "top="+top);
        } else {
            layoutParams.bottomMargin = bottom;
            Log.i(TAG_LOG_BIO, "bottom="+bottom);
        }

        btn_press.setLayoutParams(layoutParams);
    }

}
