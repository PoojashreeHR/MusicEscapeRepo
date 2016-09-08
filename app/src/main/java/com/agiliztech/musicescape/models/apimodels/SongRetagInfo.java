package com.agiliztech.musicescape.models.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Asif on 18-08-2016.
 */
public class SongRetagInfo implements Parcelable {

    @SerializedName("song")
    @Expose
    int serverSongId;
    @SerializedName("energy")
    @Expose
    double energy;
    @SerializedName("valence")
    @Expose
    double valence;
    @SerializedName("mood")
    @Expose
    String mood;

    protected SongRetagInfo(Parcel in) {
        serverSongId = in.readInt();
        energy = in.readDouble();
        valence = in.readDouble();
        mood = in.readString();
    }

    public SongRetagInfo() {
    }

    public static final Creator<SongRetagInfo> CREATOR = new Creator<SongRetagInfo>() {
        @Override
        public SongRetagInfo createFromParcel(Parcel in) {
            return new SongRetagInfo(in);
        }

        @Override
        public SongRetagInfo[] newArray(int size) {
            return new SongRetagInfo[size];
        }
    };

    public int getSong() {
        return serverSongId;
    }

    public void setSong(int song) {
        this.serverSongId = song;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getValence() {
        return valence;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(serverSongId);
        dest.writeDouble(energy);
        dest.writeDouble(valence);
        dest.writeString(mood);
    }
}
