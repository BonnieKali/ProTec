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

/**
 * James Hanratty (s1645821)
 * This file contains static methods that help with location related functionality
 */
public class LocatorHelper {

    private static final String TAG = "LocatorHelper";

    /**
     * Loads the patients and displays them on the map
     * @param mMap: The google map
     */
    public static void loadAndDisplayPatients(UserSession user, GoogleMap mMap){
        // We will display patients if the user is a carer
        if (user.getType() == UserInfo.UserType.CARER){
            HashSet<String> patient_ids = ((CarerSession) user).carerData.relationship.getPatientIDs();
            Log.d(TAG,"Patient Ids assigned to carer: " + patient_ids);

            // background process to get patients from carers
            RunnableTask get_patients = () ->
                    new TaskResult<HashSet>(getPatientsForCarer((CarerSession) user));

            // method called when get_patients has completeds
            OnTaskCompleteCallback callback = taskResult -> {
                showPatients((HashSet<PatientSession>) taskResult.getData(), mMap);
            };

            // run the task
            BackgroundPool.attachTask(get_patients, callback);
        }
    }

    /**
     * This is the method the background task runs which retrieves the
     * patients of the carer
     * @param user: The carer
     * @return: All the patient sessions for the carer
     */
    private static HashSet<PatientSession> getPatientsForCarer(CarerSession user) {
        Log.d(TAG,"Getting patients for carer: " + user.userInfo.getUserName());
        HashSet<PatientSession> patients = user.carerData.getAssignedPatientSessions();
        return patients;
    }

    /**
     * This shows the patients on the map
     * @param patientSessions: All the patient sessions which are to be shown on the map
     * @param mMap: The google map
     */
    private static void showPatients(HashSet<PatientSession> patientSessions, GoogleMap mMap) {
        Log.d(TAG,"Shwoing patients on map");
        for (PatientSession s : patientSessions) {
            if (s.patientData.locationData.getLastKnownLocation() != null) {
                myLocation latestLocation = s.patientData.locationData.getLastKnownLocation();
                String title = s.userInfo.getUserName();

                MarkerOptions markerOptions = new MarkerOptions().
                        position(latestLocation.getLatLng()).
                        title(title).
                        snippet("Last seen: " + latestLocation.getPrettyDateTime()).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mMap.addMarker(markerOptions);
                Log.d(TAG, "Added Marker for patient: " + title);
            }
        }
    }

    /**
     * Used to create an approprtiate bitmap icon to be used as an icon for a marker.
     * @param context
     * @param vectorDrawableResourceId: The image resource id to load as a bitmap
     * @return: A bitmapDescriptor which can be used to set a marker icon
     */
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
