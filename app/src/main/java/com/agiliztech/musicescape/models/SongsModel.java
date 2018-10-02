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
    private String filepath;
    String albumName;
    String songMood;

    public SongsModel(){}
    public SongsModel(long id, String title, String artist, String filepath, String albumName) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.filepath = filepath;
        this.albumName = albumName;
    }

    protected SongsModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
        filepath = in.readString();
        albumName = in.readString();
        songMood = in.readString();
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

    public String getSongMood() {
        return songMood;
    }

    public void setSongMood(String songMood) {
        this.songMood = songMood;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SongsModel) && this.id == ((SongsModel) obj).getId()
                && this.title.equals(((SongsModel) obj).getTitle())
                && this.artist.equals(((SongsModel) obj).getArtist())
                && this.albumName.equals(((SongsModel) obj).getAlbumName());
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
        dest.writeString(albumName);
        dest.writeString(songMood);
    }
}