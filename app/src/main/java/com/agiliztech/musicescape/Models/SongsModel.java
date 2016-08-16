package com.agiliztech.musicescape.models;

/**
 * Created by Pooja on 11-08-2016.
 */
public class SongsModel
{
    long id;
    String title;
    String artist;
    private String filepath;

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
    public SongsModel(long songID, String songTitle, String songArtist,String fPath)
    {
        id=songID;
        title=songTitle;
        artist=songArtist;
        filepath = fPath;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
