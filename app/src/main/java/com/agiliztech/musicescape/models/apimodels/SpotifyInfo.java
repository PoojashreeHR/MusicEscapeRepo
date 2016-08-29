package com.agiliztech.musicescape.models.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Asif on 18-08-2016.
 */
public class SpotifyInfo implements Parcelable {
    @SerializedName("id")
    @Expose
    int id;

    @SerializedName("spotifyId")
    @Expose
    String spotifyId;

    protected SpotifyInfo(Parcel in) {
        id = in.readInt();
        spotifyId = in.readString();
    }

    public SpotifyInfo(){}

    public static final Creator<SpotifyInfo> CREATOR = new Creator<SpotifyInfo>() {
        @Override
        public SpotifyInfo createFromParcel(Parcel in) {
            return new SpotifyInfo(in);
        }

        @Override
        public SpotifyInfo[] newArray(int size) {
            return new SpotifyInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(spotifyId);
    }
}
