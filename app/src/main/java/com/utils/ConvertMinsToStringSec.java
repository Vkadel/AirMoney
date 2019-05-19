package com.utils;

import android.content.Context;

import com.vkreated.airmoney.R;

public class ConvertMinsToStringSec {
    String theTime;
    int hours;
    int mins;

    public ConvertMinsToStringSec(double time, Context context) {
        hours = (int) (time / 60);
        mins = (int) time - hours * 60;
        theTime = context.getString(R.string.time_format_String, String.valueOf(hours), String.valueOf(mins));
    }

    public String getTheTime() {
        return theTime;
    }
}
