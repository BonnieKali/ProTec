package com.example.dashboard.patient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.patient.PatientSession;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.TaskResult;
import com.google.android.material.slider.Slider;

import java.util.Map;

/**
 * create an instance of this fragment.
 */
public class PatientEditFragment extends Fragment {
    // Log
    String TAG_LOG =  "PatientEditFragment";
    // Session
    Session session;
    PatientSession patientSession;
    // Class
    View view;
    // Ui
    TextView tv_acc, tv_q1, tv_q2;
    Slider slider_acc, slider_q1, slider_q2;
    Button btn_update_settings, btn_set_default, btn_set_low, btn_set_high;
    CheckBox cb_fallen_state;
    // Settings that can be edited
    double acc_threshold, q1_threshold, q2_threshold;
    // Enums
    protected enum SETTINGS_KEY {
        ACC("ACC"), Q1("Q1"), Q2("Q2"), FALLEN("Fallen");
        private final String key;
        /**
         * @param key
         */
        private SETTINGS_KEY(final String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }
    }
    private enum STATE_FALLEN {
        TRUE(true), FALSE(false);
        private final boolean key;
        /**
         * @param key
         */
        private STATE_FALLEN(final boolean key) {
            this.key = key;
        }
        public boolean getKey() {
            return key;
        }
    }
    private enum SETTINGS_VALUES_TYPES{DEFAULT,HIGH,LOW};
    private enum SETTINGS_VALUES {
        ACC_DEFAULT(30.), Q1_DEFAULT(40.), Q2_DEFAULT(40.),
        ACC_HIGH(60.), Q1_HIGH(70.), Q2_HIGH(70.),
        ACC_LOW(10.), Q1_LOW(30.), Q2_LOW(30.);
        private final double key;
        /**
         * @param key
         */
        private SETTINGS_VALUES(final double key) {
            this.key = key;
        }
        public double getKey() {
            return key;
        }
    }
    // Constants
    boolean cb_fallen_state_value;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_patient_edit, container, false);
        // Initialize Patient session
        initializePatient();
        return view;
    }


    /***
     * Function Initializes all patient session requirements
     */
    private void initializePatient() {
        session = Session.getInstance();
        patientSession = getArguments().getParcelable("patientSession");                       // get user session
        ((TextView)view.findViewById(R.id.patient_textView)).setText(patientSession.userInfo.getUserName()); // set user name on ui
        session.getPatientSettings(patientSession.userInfo.id, new OnTaskCompleteCallback() {
            @Override
            public void onComplete(TaskResult<?> taskResult) {
                Map<String, Object> settings = (Map<String, Object>) taskResult.getData();
                Log.d(TAG_LOG,"LOADING PATIENT SETTINGS"+settings.toString());
                loadPatientSettings(settings);                                                      // extract settings from database
                initializeUi();
            }
        });
    }


    private void loadPatientSettings(Map<String, Object> settings) {
        // check if settings exists
        if (isValidSettings(settings)) {                                                            // all thresholds already defined in database
            Log.d(TAG_LOG,"SETTINGS EXIST"+settings.toString());
            acc_threshold = Double.valueOf(settings.get(SETTINGS_KEY.ACC.getKey()).toString());
            q1_threshold = Double.valueOf(settings.get(SETTINGS_KEY.Q1.getKey()).toString());
            q2_threshold = Double.valueOf(settings.get(SETTINGS_KEY.Q2.getKey()).toString());
            cb_fallen_state_value = Boolean.parseBoolean(settings.get(SETTINGS_KEY.FALLEN.getKey()).toString());
            Log.d(TAG_LOG,"VALUES WERE SET: "+acc_threshold+"; "+"q1_threshold="+q1_threshold+"; "+"q2_threshold="+q2_threshold);
        } else {                                                                                    // set them in database
            Log.d(TAG_LOG,"SETTINGS DON'T EXIST");
            loadValues(SETTINGS_VALUES_TYPES.DEFAULT);
        }

    }


    /***
     * Loads default values in class and updates the database
     */
    private void loadValues(SETTINGS_VALUES_TYPES type) {
        if(type == SETTINGS_VALUES_TYPES.DEFAULT) {
            acc_threshold = SETTINGS_VALUES.ACC_DEFAULT.getKey();
            q1_threshold = SETTINGS_VALUES.Q1_DEFAULT.getKey();
            q2_threshold = SETTINGS_VALUES.Q2_DEFAULT.getKey();
        } else if (type == SETTINGS_VALUES_TYPES.HIGH){
            acc_threshold = SETTINGS_VALUES.ACC_HIGH.getKey();
            q1_threshold = SETTINGS_VALUES.Q1_HIGH.getKey();
            q2_threshold = SETTINGS_VALUES.Q2_HIGH.getKey();
        } else if (type == SETTINGS_VALUES_TYPES.LOW){
            acc_threshold = SETTINGS_VALUES.ACC_LOW.getKey();
            q1_threshold = SETTINGS_VALUES.Q1_LOW.getKey();
            q2_threshold = SETTINGS_VALUES.Q2_LOW.getKey();
        }
        updateSettingsDatabase();
    }


    /***
     * Updates the Database with the new settings
     */
    private void updateSettingsDatabase() {
        session.setPatientSetting(patientSession.userInfo.id,SETTINGS_KEY.ACC.getKey(),acc_threshold);
        session.setPatientSetting(patientSession.userInfo.id,SETTINGS_KEY.Q1.getKey(),q1_threshold);
        session.setPatientSetting(patientSession.userInfo.id,SETTINGS_KEY.Q2.getKey(),q2_threshold);
    }


    /***
     * Function checks if the keys are present in the database
     * @param settings
     * @return
     */
    private boolean isValidSettings(Map<String, Object> settings) {
        for (SETTINGS_KEY settings_key : SETTINGS_KEY.values()) {
            if (!settings.containsKey(settings_key.getKey())){
                return false;
            }
        }
        return true;
    }


    /***
     * Function Initializes the Ui Elements
     */
    private void initializeUi() {
        initializeUiVariables();
        setUiElements();
        setUiListeners();
    }


    private void setUiListeners() {
        setSliderListeners();
        setButtonListeners();
        setCheckBoxListeners();
    }


    private void setCheckBoxListeners() {
        cb_fallen_state.setOnClickListener(v -> {
            cb_fallen_state_value = !(cb_fallen_state_value);
            updateFallenSetting();
        });
    }


    /***
     * Updates the value of the fallen state in the database
     */
    private void updateFallenSetting() {
        session.setPatientSetting(patientSession.userInfo.id,SETTINGS_KEY.FALLEN.getKey(),cb_fallen_state_value);
    }


    private void setButtonListeners() {
        btn_update_settings.setOnClickListener(v -> {
            updateSettingsDatabase();
        });
        btn_set_default.setOnClickListener(v -> {
            loadValues(SETTINGS_VALUES_TYPES.DEFAULT);
            setUiElements();
        });
        btn_set_high.setOnClickListener(v -> {
            loadValues(SETTINGS_VALUES_TYPES.HIGH);
            setUiElements();
        });
        btn_set_low.setOnClickListener(v -> {
            loadValues(SETTINGS_VALUES_TYPES.LOW);
            setUiElements();
        });
    }


    /***
     * Implements the Listeners for the Sliders
     */
    private void setSliderListeners() {
        slider_acc.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tv_acc.setText(""+value);
                acc_threshold = (double) value;
            }
        });
        slider_q1.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tv_q1.setText(""+value);
                q1_threshold = (double) value;
            }
        });
        slider_q2.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tv_q2.setText(""+value);
                q2_threshold = (double) value;
            }
        });
    }


    /***
     * Sets the initial values of the Ui Elements
     */
    private void setUiElements() {
        Log.d(TAG_LOG,"VALUES WERE SET: "+acc_threshold+"; "+"q1_threshold="+q1_threshold+"; "+"q2_threshold="+q2_threshold);
        slider_acc.setValue((float)acc_threshold);
        slider_q1.setValue((float)q1_threshold);
        slider_q2.setValue((float)q2_threshold);
        tv_acc.setText(("")+(int)acc_threshold);
        tv_q1.setText(("")+(int)q1_threshold);
        tv_q2.setText(("")+(int)q2_threshold);
        cb_fallen_state.setChecked(cb_fallen_state_value);
    }


    /***
     * Creates a reference to necessary UI elements
     */
    private void initializeUiVariables() {
        tv_acc = view.findViewById(R.id.tv_slider_thresh_acc_value);
        tv_q1 = view.findViewById(R.id.tv_slider_thresh_q1_value);
        tv_q2 = view.findViewById(R.id.tv_slider_thresh_q2_value);
        slider_acc = view.findViewById(R.id.slider_thresh_acc);
        slider_q1 = view.findViewById(R.id.slider_thresh_q1);
        slider_q2 = view.findViewById(R.id.slider_thresh_q2);
        btn_update_settings = view.findViewById(R.id.btn_update_settings);
        btn_set_default = view.findViewById((R.id.btn_sensitive_default_settings));
        btn_set_low = view.findViewById(R.id.btn_sensitive_low_settings);
        btn_set_high = view.findViewById(R.id.btn_sensitive_high_settings);
        cb_fallen_state = view.findViewById(R.id.checkBox_fall_status);
    }
}