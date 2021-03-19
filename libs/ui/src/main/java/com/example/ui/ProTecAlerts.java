package com.example.ui;

import android.app.AlertDialog;
import android.content.Context;


public class ProTecAlerts {

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

    // ALERT EXAMPLE
//    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//    builder1.setMessage("Write your message here.");
//    builder1.setCancelable(true);
//
//    builder1.setPositiveButton(
//            "Yes",
//            new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//
//    builder1.setNegativeButton(
//            "No",
//            new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//
//        AlertDialog alert11 = builder1.create();
//    alert11.show();

}
