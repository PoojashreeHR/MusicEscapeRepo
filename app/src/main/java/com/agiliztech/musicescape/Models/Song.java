package com.agiliztech.musicescape.Models;

import java.util.List;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class Song {

    private int acousticness;
    private String analysisStatus;
    private String artistNameFirstLetter;
    private int danceability;
    private int durationEN;
    private int energy;
    private String genre;
    private int key;
    private int liveness;
    private int loudness;
    private String md5Hash;
    private int mode;
    private int mood;
    private int pID;
    private int playbackDuration;
    private int scannedMood;
    private int serverID;
    private int songDeleted;
    private String songName;
    private String songNameEN;
    private String songNameFirstLetter;
    private String songURL;
    private int speechiness;
    private int tempo;
    private int userChangedEnergy;
    private int userChangedMood;
    private int userChangedValance;
    private String userRetaggedMood;
    private int valance;
    private Album album;
    private Artist artist;
    private List<JourneySongs>  journeySongs;

    public int getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(int acousticness) {
        this.acousticness = acousticness;
    }

    public String getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(String analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public String getArtistNameFirstLetter() {
        return artistNameFirstLetter;
    }

    public void setArtistNameFirstLetter(String artistNameFirstLetter) {
        this.artistNameFirstLetter = artistNameFirstLetter;
    }

    public int getDanceability() {
        return danceability;
    }

    public void setDanceability(int danceability) {
        this.danceability = danceability;
    }

    public int getDurationEN() {
        return durationEN;
    }

    public void setDurationEN(int durationEN) {
        this.durationEN = durationEN;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getLiveness() {
        return liveness;
    }

    public void setLiveness(int liveness) {
        this.liveness = liveness;
    }

    public int getLoudness() {
        return loudness;
    }

    public void setLoudness(int loudness) {
        this.loudness = loudness;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }


    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getpID() {
        return pID;
    }

    public void setpID(int pID) {
        this.pID = pID;
    }

    public int getPlaybackDuration() {
        return playbackDuration;
    }

    public void setPlaybackDuration(int playbackDuration) {
        this.playbackDuration = playbackDuration;
    }

    public int getScannedMood() {
        return scannedMood;
    }

    public void setScannedMood(int scannedMood) {
        this.scannedMood = scannedMood;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public int getSongDeleted() {
        return songDeleted;
    }

    public void setSongDeleted(int songDeleted) {
        this.songDeleted = songDeleted;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongNameEN() {
        return songNameEN;
    }

    public void setSongNameEN(String songNameEN) {
        this.songNameEN = songNameEN;
    }

    public String getSongNameFirstLetter() {
        return songNameFirstLetter;
    }

    public void setSongNameFirstLetter(String songNameFirstLetter) {
        this.songNameFirstLetter = songNameFirstLetter;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

    public int getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(int speechiness) {
        this.speechiness = speechiness;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public int getUserChangedEnergy() {
        return userChangedEnergy;
    }

    public void setUserChangedEnergy(int userChangedEnergy) {
        this.userChangedEnergy = userChangedEnergy;
    }

    public int getUserChangedMood() {
        return userChangedMood;
    }

    public void setUserChangedMood(int userChangedMood) {
        this.userChangedMood = userChangedMood;
    }

    public int getUserChangedValance() {
        return userChangedValance;
    }

    public void setUserChangedValance(int userChangedValance) {
        this.userChangedValance = userChangedValance;
    }

    public String getUserRetaggedMood() {
        return userRetaggedMood;
    }

    public void setUserRetaggedMood(String userRetaggedMood) {
        this.userRetaggedMood = userRetaggedMood;
    }

    public int getValance() {
        return valance;
    }

    public void setValance(int valance) {
        this.valance = valance;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public List<JourneySongs> getJourneySongs() {
        return journeySongs;
    }

    public void setJourneySongs(List<JourneySongs> journeySongs) {
        this.journeySongs = journeySongs;
    }
}
