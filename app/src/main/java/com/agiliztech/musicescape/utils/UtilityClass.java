package com.agiliztech.musicescape.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
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


}
