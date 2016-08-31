package com.agiliztech.musicescape.models.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Asif on 30-08-2016.
 */
public class SongRetagMain implements Parcelable{
    @SerializedName("user")
    @Expose
    String deviceId;
    @SerializedName("songs")
    @Expose
    ArrayList<SongRetagInfo> retagList;

    protected SongRetagMain(Parcel in) {
        deviceId = in.readString();
        retagList = in.createTypedArrayList(SongRetagInfo.CREATOR);
    }

    public static final Creator<SongRetagMain> CREATOR = new Creator<SongRetagMain>() {
        @Override
        public SongRetagMain createFromParcel(Parcel in) {
            return new SongRetagMain(in);
        }

        @Override
        public SongRetagMain[] newArray(int size) {
            return new SongRetagMain[size];
        }
    };


    public SongRetagMain(String deviceId, ArrayList<SongRetagInfo> retagList) {
        this.deviceId = deviceId;
        this.retagList = retagList;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ArrayList<SongRetagInfo> getRetagList() {
        return retagList;
    }

    public void setRetagList(ArrayList<SongRetagInfo> retagList) {
        this.retagList = retagList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeTypedList(retagList);
    }
}
