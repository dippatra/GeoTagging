package com.geotagging.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

public class CommonMethods {
    private static final String TAG=CommonMethods.class.getSimpleName();


    public static void setFontRegular(TextView textView) {
        try {
            if(textView!=null){
                Typeface typeface = Typefaces.get(textView.getContext(), "fonts/SF-Pro-Text-Regular.otf");
                textView.setTypeface(typeface);
            }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());

        }
    }

    public static void setFontMedium(TextView textView) {
        try {
            if(textView!=null){
                Typeface typeface = Typefaces.get(textView.getContext(), "fonts/SF-Pro-Text-Medium.otf");
                textView.setTypeface(typeface);
            }


        } catch (Exception e) {
           Log.e(TAG,e.getMessage());

        }
    }
    public static void setFontBold(TextView textView) {
        try {
            if(textView!=null){
                Typeface typeface = Typefaces.get(textView.getContext(), "fonts/SF-Pro-Text-Bold.otf");
                textView.setTypeface(typeface);
            }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());

        }
    }

}
