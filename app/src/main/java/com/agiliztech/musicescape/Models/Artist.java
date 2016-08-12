package com.agiliztech.musicescape.models;

import java.util.List;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class Artist {

    private String artistIDEN;
    private String artistName;
    private int artistID;
    private String artistNameEN;
    private Album albums;
    private List<Song> songs;

    public String getArtistIDEN() {
        return artistIDEN;
    }

    public void setArtistIDEN(String artistIDEN) {
        this.artistIDEN = artistIDEN;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }


    public String getArtistNameEN() {
        return artistNameEN;
    }

    public void setArtistNameEN(String artistNameEN) {
        this.artistNameEN = artistNameEN;
    }

    public Album getAlbums() {
        return albums;
    }

    public void setAlbums(Album albums) {
        this.albums = albums;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }
}
