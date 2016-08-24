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
    String album;
    private String filepath;

    protected SongsModel(Parcel in) {
        id = in.readLong();
        album=in.readString();
        album = in.readString();
        title = in.readString();
        artist = in.readString();
        filepath = in.readString();
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public SongsModel(long songID, String songTitle, String songArtist,String fPath,String songAlbum)
    {
        id=songID;
        title=songTitle;
        album=songAlbum;
        artist=songArtist;
        filepath = fPath;
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
        dest.writeString(filepath);
        dest.writeString(album);
    }
}