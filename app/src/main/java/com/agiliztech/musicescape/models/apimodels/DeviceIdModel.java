package com.agiliztech.musicescape.models.apimodels;

/**
 * Created by Asif on 19-08-2016.
 */
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DeviceIdModel {

    @SerializedName("user")
    private String user;
    @SerializedName("songs")
    private List<SongRequest> songs = new ArrayList<SongRequest>();

    public DeviceIdModel(String user, List<SongRequest> songs) {
        this.user = user;
        this.songs = songs;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<SongRequest> getSongs() {
        return songs;
    }

    public void setSongs(List<SongRequest> songs) {
        this.songs = songs;
    }

}