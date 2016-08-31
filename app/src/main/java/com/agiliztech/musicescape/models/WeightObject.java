package com.agiliztech.musicescape.models;

/**
 * Created by praburaam on 30/08/16.
 */
public class WeightObject {
    private int weight;
    private int Value;
    private int mood;
    private int songIndex;
    private Song songObj;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    public Song getSongObj() {
        return songObj;
    }

    public void setSongObj(Song songObj) {
        this.songObj = songObj;
    }
}
