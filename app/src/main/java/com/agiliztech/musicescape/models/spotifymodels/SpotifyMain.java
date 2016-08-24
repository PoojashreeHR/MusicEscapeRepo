package com.agiliztech.musicescape.models.spotifymodels;

/**
 * Created by Asif on 23-08-2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpotifyMain{

    @SerializedName("tracks")
    @Expose
    private Tracks tracks;

    /**
     *
     * @return
     * The tracks
     */
    public Tracks getTracks() {
        return tracks;
    }

    /**
     *
     * @param tracks
     * The tracks
     */
    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

}