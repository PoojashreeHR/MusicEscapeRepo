package com.agiliztech.musicescape.models.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asif on 24-08-2016.
 */
public class SpotifyModelMain implements Parcelable {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("songs")
    @Expose
    private List<SpotifyInfo> songs;

    public SpotifyModelMain(String user, List<SpotifyInfo> songs) {
        this.user = user;
        this.songs = songs;
    }

    protected SpotifyModelMain(Parcel in) {
        user = in.readString();
        songs = in.createTypedArrayList(SpotifyInfo.CREATOR);
    }

    public static final Creator<SpotifyModelMain> CREATOR = new Creator<SpotifyModelMain>() {
        @Override
        public SpotifyModelMain createFromParcel(Parcel in) {
            return new SpotifyModelMain(in);
        }

        @Override
        public SpotifyModelMain[] newArray(int size) {
            return new SpotifyModelMain[size];
        }
    };

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<SpotifyInfo> getSongs() {
        return songs;
    }

    public void setSongs(List<SpotifyInfo> songs) {
        this.songs = songs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeTypedList(songs);
    }
}
