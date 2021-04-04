package com.example.ui;

import android.app.AlertDialog;
import android.content.Context;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * This cals is responsible for displaying alert dialogs to the User.
 */


public class ProTecAlerts {

    /**
     * Spawns a warning alert
     *
     * @param context Application context
     * @param msg Message to be displayed
     */
    public static void warning(Context context, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Ok",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

}
