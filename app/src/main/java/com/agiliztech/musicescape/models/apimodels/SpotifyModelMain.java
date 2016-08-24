package com.agiliztech.musicescape.models.apimodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asif on 24-08-2016.
 */
public class SpotifyModelMain {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("songs")
    @Expose
    private List<SpotifyInfo> songs = new ArrayList<SpotifyInfo>();

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<SpotifyInfo> getSongs() {
        return songs;
    }

    public void setSongs(List<SpotifyInfo> songs) {
        this.songs = songs;
    }

}
