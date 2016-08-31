package com.agiliztech.musicescape.models;

import java.util.List;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class Album {
    private String albumTitle;
    private String albumTitleEN;
    private int albumID;
    private Artist artists;
    private List<Song> songs;

    public Album(String albumId, String albumName) {
        this.albumID = Integer.parseInt(albumId);
        this.albumTitle = albumName;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumTitleEN() {
        return albumTitleEN;
    }

    public void setAlbumTitleEN(String albumTitleEN) {
        this.albumTitleEN = albumTitleEN;
    }




    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Artist getArtists() {
        return artists;
    }

    public void setArtists(Artist artists) {
        this.artists = artists;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }
}
