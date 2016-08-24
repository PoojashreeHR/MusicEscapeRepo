package com.agiliztech.musicescape.models.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asif on 18-08-2016.
 */
public class ResponseSongPollModel implements  Parcelable{
    @SerializedName("batch")
    String batch;
    @SerializedName("batchSize")
    int batchSize;
    @SerializedName("since")
    String since;
    @SerializedName("songs")
    List<SongInfo> songs;
    @SerializedName("completed")
    String completed;
    @SerializedName("status")
    String status;
    @SerializedName("identifiedCount")
    int identifiedCount;
    @SerializedName("identifyingCount")
    int identifyingCount;
    @SerializedName("error")
    ErrorObject error;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdentifiedCount() {
        return identifiedCount;
    }

    public void setIdentifiedCount(int identifiedCount) {
        this.identifiedCount = identifiedCount;
    }

    public int getIdentifyingCount() {
        return identifyingCount;
    }

    public void setIdentifyingCount(int identifyingCount) {
        this.identifyingCount = identifyingCount;
    }

    public ErrorObject getError() {
        return error;
    }

    public void setError(ErrorObject error) {
        this.error = error;
    }

    public List<SongInfo> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<SongInfo> songs) {
        this.songs = songs;
    }

    protected ResponseSongPollModel(Parcel in) {
        batch = in.readString();
        batchSize = in.readInt();
        since = in.readString();
        completed = in.readString();
        status = in.readString();
        identifiedCount = in.readInt();
        identifyingCount = in.readInt();
        error = in.readParcelable(ErrorObject.class.getClassLoader());
        songs = in.createTypedArrayList(SongInfo.CREATOR);
    }

    public static final Creator<ResponseSongPollModel> CREATOR = new Creator<ResponseSongPollModel>() {
        @Override
        public ResponseSongPollModel createFromParcel(Parcel in) {
            return new ResponseSongPollModel(in);
        }

        @Override
        public ResponseSongPollModel[] newArray(int size) {
            return new ResponseSongPollModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(batch);
        dest.writeInt(batchSize);
        dest.writeString(since);
        dest.writeString(completed);
        dest.writeString(status);
        dest.writeInt(identifiedCount);
        dest.writeInt(identifyingCount);
        dest.writeParcelable(error, flags);
        dest.writeTypedList(songs);
    }
}
