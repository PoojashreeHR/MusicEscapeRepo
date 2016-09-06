package com.agiliztech.musicescape.models;

/**
 * Created by praburaam on 06/09/16.
 */
public class SongUiObj {
    private boolean song;
    private Song songObj;
    private String alphabet;

    public SongUiObj(boolean b, Song s) {
        song = b;
        songObj = s;
    }

    public SongUiObj(boolean b, String alpha){
        song = b;
        alphabet = alpha;
    }

    public boolean isSong() {
        return song;
    }

    public void setSong(boolean song) {
        this.song = song;
    }

    public Song getSongObj() {
        return songObj;
    }

    public void setSongObj(Song songObj) {
        this.songObj = songObj;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }
}
