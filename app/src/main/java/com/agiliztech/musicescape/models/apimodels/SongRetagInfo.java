package com.agiliztech.musicescape.models.apimodels;

/**
 * Created by Asif on 18-08-2016.
 */
public class SongRetagInfo {

    Integer song;
    Double energy;
    Double valence;
    String mood;

    public Integer getSong() {
        return song;
    }

    public void setSong(Integer song) {
        this.song = song;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    public Double getValence() {
        return valence;
    }

    public void setValence(Double valence) {
        this.valence = valence;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}
