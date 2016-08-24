package com.agiliztech.musicescape.models.spotifymodels;

/**
 * Created by Asif on 23-08-2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExternalUrls_ {

    @SerializedName("spotify")
    @Expose
    private String spotify;

    /**
     *
     * @return
     * The spotify
     */
    public String getSpotify() {
        return spotify;
    }

    /**
     *
     * @param spotify
     * The spotify
     */
    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

}