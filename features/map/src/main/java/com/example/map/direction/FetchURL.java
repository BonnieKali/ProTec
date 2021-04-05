package com.example.map.direction;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * James Hanratty (s1645821) with credit to Vishal.
 * This file contains class that queryies the Google Directions API
 * which must be done in an AsyncTask
 */

public class FetchURL extends AsyncTask<String, Void, String> {
    private static final String TAG = "myDirection";

    Context mContext;
    String directionMode = "driving";

    public FetchURL(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Stores the data from repsonse of executing url (Directions API)
     * @param strings: The URLs to execute
     * @return: The data from the url
     */
    @Override
    protected String doInBackground(String... strings) {
        // For storing data from web service
        String data = "";
        directionMode = strings[1];
        try {
            // Fetching the data from web service
            data = downloadUrl(strings[0]);
            Log.d(TAG, "Background task data " + data.toString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return data;
    }

    /**
     * Parse the points received from the response of the directions API
     * @param s: The directions api result from asking for directions
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(mContext, directionMode);
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s);
    }

    /**
     * Actually makes the
     * @param strUrl: The url to execute
     * @return: The data from the response of executing the URL
     * @throws IOException: The url request threw an error
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d(TAG, "Downloaded URL: " + data.toString());
            br.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception downloading URL: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}

