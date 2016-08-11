package com.agiliztech.musicescape.Models;

/**
 * Created by Pooja on 11-08-2016.
 */
public class SongsModel
{
    long id;
    String title;
    String artist;

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

}
