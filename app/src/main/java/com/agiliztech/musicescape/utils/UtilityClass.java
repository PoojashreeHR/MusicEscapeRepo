package com.agiliztech.musicescape.utils;

/**
 * Created by Asif on 12-08-2016.
 */
public class UtilityClass {
    public static int progressToTimer(int progress,int totalDuration){
        int currentDuration = 0 ;
        totalDuration = (int) (totalDuration/1000);
        currentDuration = (int)((((double)progress)/100)*totalDuration);
        return currentDuration * 1000;
    }

    public static int getProgressPercentage(long currDuration,long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds = (int) (currDuration/1000);
        int totalSeconds = (int) (totalDuration/1000);

        percentage = (((double)currentSeconds)/totalSeconds)*100;
        return percentage.intValue();
    }

}
