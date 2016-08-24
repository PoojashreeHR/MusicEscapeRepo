package com.agiliztech.musicescape.models.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Asif on 18-08-2016.
 */
public class SongInfo implements Parcelable {
    @SerializedName("id")
    int id;

    @SerializedName("clientId")
    String clientId;

    @SerializedName("spotifyId")
    String spotifyId;

    @SerializedName("mood")
    String mood;

    @SerializedName("energy")
    double energy;

    @SerializedName("valence")
    double valence;

    @SerializedName("echonestAnalysisStatus")
    String echonestAnalysisStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
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

    public String getEchonestAnalysisStatus() {
        return echonestAnalysisStatus;
    }

    public void setEchonestAnalysisStatus(String echonestAnalysisStatus) {
        this.echonestAnalysisStatus = echonestAnalysisStatus;
    }

    protected SongInfo(Parcel in) {
        id = in.readInt();
        clientId = in.readString();
        spotifyId = in.readString();
        mood = in.readString();
        energy = in.readDouble();
        valence = in.readDouble();
        echonestAnalysisStatus = in.readString();
    }

    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel in) {
            return new SongInfo(in);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(clientId);
        dest.writeString(spotifyId);
        dest.writeString(mood);
        dest.writeDouble(energy);
        dest.writeDouble(valence);
        dest.writeString(echonestAnalysisStatus);
    }
}
