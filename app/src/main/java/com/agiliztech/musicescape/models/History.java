package com.agiliztech.musicescape.models;

import com.agiliztech.musicescape.journey.SongMoodCategory;

import java.util.Date;
import java.util.List;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class History {
    SongMoodCategory CurrentMood, TargetMood;
    public History(SongMoodCategory currentMood, SongMoodCategory targetMood) {
        CurrentMood = currentMood;
        TargetMood = targetMood;
    }

    public History(){}


    public SongMoodCategory getCurrentMood() {
        return CurrentMood;
    }

    public void setCurrentMood(SongMoodCategory currentMood) {
        CurrentMood = currentMood;
    }

    public SongMoodCategory getTargetMood() {
        return TargetMood;
    }

    public void setTargetMood(SongMoodCategory targetMood) {
        TargetMood = targetMood;
    }
}
