package com.agiliztech.musicescape.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pooja on 11-08-2016.
 */
public class SongsModel implements Parcelable
{
    long id;
    String title;
    String artist;

    protected SongsModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
    }

    public static final Creator<SongsModel> CREATOR = new Creator<SongsModel>() {
        @Override
        public SongsModel createFromParcel(Parcel in) {
            return new SongsModel(in);
        }

        @Override
        public SongsModel[] newArray(int size) {
            return new SongsModel[size];
        }
    };

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public SongsModel(long songID, String songTitle, String songArtist)
    {
        id=songID;
        title=songTitle;
        artist=songArtist;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
    }
}
