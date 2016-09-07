package com.agiliztech.musicescape.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by Asif on 12-08-2016.
 */
public class UtilityClass {

    static Gson gson = new Gson();

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }

    public static int getProgressPercentage(long currDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currDuration / 1000);
        int totalSeconds = (int) (totalDuration / 1000);

        percentage = (((double) currentSeconds) / totalSeconds) * 100;
        return percentage.intValue();
    }

    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        Log.e("DEVICE ID", "" + deviceId);
        return deviceId;

    }

    public static Gson getJsonConvertor() {
        return gson;
    }

    public static void log(String tagName, String message) {
        Log.d(tagName, message);
    }

    public static boolean checkInternetConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected()));
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int devSize = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int height = metrics.heightPixels;

        if(height > 1280){
            px *=3;
        }
        else if(height > 800 && height <= 1280){
            px *=2;
        }
        else if(height > 500 && height <= 800){
            px *=1;
        }
        else{
            px *=0.5;
        }

//        if (devSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
//            px *= 3;
//        }
//        else if (devSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
//            px *= 2;
//        }
//        else if (devSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
//            px *= 1;
//        }


        //float dp = px / ((float)metrics.density);
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertPixelsToDpWidth(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int devSize = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int width = metrics.widthPixels;

        if(width > 1280){
            px *=0.5;
        }
        else if(width > 800 && width <= 1280){
            px *=0.67;
        }
        else if(width > 500 && width <= 800){
            px *=1;
        }
        else{
            px *=2;
        }

//        if (devSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
//            px *= 3;
//        }
//        else if (devSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
//            px *= 2;
//        }
//        else if (devSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
//            px *= 1;
//        }


        //float dp = px / ((float)metrics.density);
       // float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


}
