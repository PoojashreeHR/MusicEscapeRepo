package com.agiliztech.musicescape.Models;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class SongInfo {

    private int pID;
    private String songName;
    private String artistName;
    private String songUrl;
    private String genre;
    private int playbackDuration;
    private String albumName;
    private int artistID;
    private int albumID;

    public int getpID() {
        return pID;
    }

    public void setpID(int pID) {
        this.pID = pID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPlaybackDuration() {
        return playbackDuration;
    }

    public void setPlaybackDuration(int playbackDuration) {
        this.playbackDuration = playbackDuration;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }
}
