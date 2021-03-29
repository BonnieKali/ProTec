package com.example.map.location;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.map.R;
import com.example.map.geofence.GeoFenceHelper;
import com.example.map.map.MapHelper;
import com.example.session.Session;
import com.example.session.user.UserInfo;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;
import com.example.session.user.data.location.myLocation;
import com.example.session.user.patient.PatientSession;
import com.example.threads.BackgroundPool;
import com.example.threads.OnTaskCompleteCallback;
import com.example.threads.RunnableTask;
import com.example.threads.TaskResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.HashSet;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class LocatorHelper {

    private static final String TAG = "LocatorHelper";

    /**
     * Loads the patients and displays them on the map
     * @param mMap
     */
    public static void loadAndDisplayPatients(UserSession user, GoogleMap mMap, Context context){
        if (user.getType() == UserInfo.UserType.PATIENT){
//            HashSet<PatientSession> patient = new HashSet<>(Arrays.asList(new PatientSession[]{(PatientSession) user}));
//            showGeofences(patient, mMap);
            // Get the carers geofences which are those of their patients
        }else if (user.getType() == UserInfo.UserType.CARER){
            HashSet<String> patient_ids = ((CarerSession) user).carerData.relationship.getPatientIDs();
            Log.d(TAG,"Patient Ids assigned to carer: " + patient_ids);

            // background process to get patients from carers
            RunnableTask get_patients = () ->
                    new TaskResult<HashSet>(getPatientsForCarer(user));

            // method called when get_patients has completeds
            OnTaskCompleteCallback callback = taskResult -> {
                showPatients((HashSet<PatientSession>) taskResult.getData(), mMap, context);
            };

            // run the task
            BackgroundPool.attachTask(get_patients, callback);
        }
    }

    /**
     * This is the method the background task runs which retrieves the
     * patients of the carer
     * @param user
     * @return
     */
    private static HashSet<PatientSession> getPatientsForCarer(UserSession user) {
        Log.d(TAG,"Getting patients for carer: " + user.userInfo.getUserName());
        HashSet<PatientSession> patients = Session.getInstance().retrieveCarerPatientSessions();
        return patients;
    }


    private static void showPatients(HashSet<PatientSession> patientSessions, GoogleMap mMap, Context context) {
        Log.d(TAG,"Shwoing patients on map");
        for (PatientSession s : patientSessions) {
            myLocation latestLocation = s.patientData.locationData.getLastKnownLocation();
            String title = s.userInfo.getUserName();

            MarkerOptions markerOptions = new MarkerOptions().
                    position(latestLocation.getLatLng()).
                    title(title).
                    snippet("Last seen: " + latestLocation.getPrettyDateTime()).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//                    icon(bitmapDescriptorFromVector(context, R.drawable.person_pin_background));
            mMap.addMarker(markerOptions);
            Log.d(TAG,"Added Marker for patient: " + title);
        }
    }

    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
