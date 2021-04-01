package com.example.session.user.data.deadreckoning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DRDataBuilder {

    /**
     * Builds a new DRData object
     * @return DRData initialized object
     */
    public static DRData buildDRData(){
        // Create a timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
        String timestamp = dateFormat.format(new Date()); // Find todays date

        return new DRData(timestamp, new ArrayList<>(), new ArrayList<>());
    }

}
